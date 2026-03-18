package com.saico.feature.dashboard

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saico.core.common.util.StepCounterSensor
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.usecase.SyncUserDataUseCase
import com.saico.core.domain.usecase.gym_exercise.GymUseCase
import com.saico.core.domain.usecase.outdoor.OutdoorUseCase
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.domain.usecase.workout.WorkoutUseCase
import com.saico.core.model.GymExercise
import com.saico.core.model.OutdoorSession
import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.model.WeightEntry
import com.saico.core.model.WorkoutSession
import com.saico.core.notification.NotificationHelper
import com.saico.feature.dashboard.state.DashboardUiState
import com.saico.feature.dashboard.state.HistoryFilter
import com.saico.feature.dashboard.util.PdfExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase,
    private val workoutUseCase: WorkoutUseCase,
    private val gymUseCase: GymUseCase,
    private val outdoorUseCase: OutdoorUseCase,
    private val stepCounterSensor: StepCounterSensor,
    private val stepCounterDataStore: StepCounterDataStore,
    private val userDataStore: UserSettingsDataStore,
    private val notificationHelper: NotificationHelper,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val database =
        FirebaseDatabase.getInstance("https://fitlog-cb7c8-default-rtdb.firebaseio.com/")

    // Bandera para evitar múltiples procesamientos de racha simultáneos
    private var isUpdatingStreak = false

    init {
        getUpdateUri()
        getUserProfile()
        initStepCounter()
        getWeeklyWorkouts()
        getHistoryData()
        getUserData()
        checkAppVersion()
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUserId.collectLatest { uid ->
                _uiState.update { it.copy(authUser = authRepository.getCurrentUser()) }
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLogin = true) }
            authRepository.loginWithGoogle(idToken).onSuccess { user ->
                syncUserDataUseCase.syncAll(user.id).onSuccess {
                    _uiState.update { it.copy(isLoadingLogin = false) }
                }.onFailure {
                    syncUserDataUseCase.restoreAllData(user.id)
                    _uiState.update { it.copy(isLoadingLogin = false) }
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingLogin = false) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }


    private fun getUpdateUri() {
        database.getReference("updateurl")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updateUrl = snapshot.getValue(String::class.java) ?: ""
                    if (updateUrl.isNotEmpty()) {
                        _uiState.update { it.copy(updateUrl = updateUrl) }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseUpdate", "Error al obtener URL: ${error.message}")
                }
            })
    }

    private fun checkAppVersion() {
        database.getReference("version").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val remoteVersion = snapshot.getValue(String::class.java)
                _uiState.update { it.copy(remoteVersion = remoteVersion) }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getUserData() {
        viewModelScope.launch {
            userDataStore.userData.collectLatest { data ->
                _uiState.update { it.copy(userData = data) }
            }
        }
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            userProfileUseCase.getUserProfileUseCase().collectLatest { profile ->
                profile?.let {
                    checkAndResetStreak(it)
                    
                    // Caso: Se abre la app y ya se cumplió la meta (con app cerrada)
                    if (it.currentStreak > it.lastStreakShown && it.currentStreak > 0 && !isUpdatingStreak) {
                        _uiState.update { state -> 
                            state.copy(
                                showLevelUp = true,
                                streakLevel = it.currentStreak
                            )
                        }
                        userProfileUseCase.updateUserProfileUseCase(it.copy(lastStreakShown = it.currentStreak))
                    }
                }
                _uiState.update { state ->
                    state.copy(userProfile = profile)
                }
            }
        }
    }

    private fun checkAndResetStreak(profile: UserProfile) {
        val today = Calendar.getInstance()
        val lastStreakDate = Calendar.getInstance().apply { timeInMillis = profile.lastStreakDate }
        
        if (profile.lastStreakDate != 0L && !isSameDay(today, lastStreakDate) && !isYesterday(today, lastStreakDate)) {
            viewModelScope.launch {
                userProfileUseCase.updateUserProfileUseCase(profile.copy(currentStreak = 0))
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(today: Calendar, other: Calendar): Boolean {
        val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(yesterday, other)
    }

    fun dismissLevelUp() {
        _uiState.update { it.copy(showLevelUp = false) }
    }

    fun updateUserProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.userProfile
            val finalProfile =
                if (currentProfile != null && currentProfile.weightKg != updatedProfile.weightKg) {
                    val newWeightEntry = WeightEntry(
                        weight = updatedProfile.weightKg,
                        date = System.currentTimeMillis()
                    )
                    updatedProfile.copy(weightHistory = updatedProfile.weightHistory + newWeightEntry)
                } else {
                    updatedProfile
                }
            userProfileUseCase.updateUserProfileUseCase(finalProfile)
            _uiState.value.authUser?.let { user ->
                syncUserDataUseCase.syncProfile(user.id, finalProfile)
            }
        }
    }

    private fun getWeeklyWorkouts() {
        viewModelScope.launch {
            workoutUseCase.getWeeklyWorkoutsUseCase().collectLatest {
                _uiState.update { state ->
                    state.copy(weeklyWorkouts = it)
                }
            }
        }
    }

    private fun getHistoryData() {
        viewModelScope.launch {
            combine(
                gymUseCase.getGymExercisesUseCase(),
                workoutUseCase.getWorkoutSessionsUseCase(),
                outdoorUseCase.getOutdoorSessionsUseCase()
            ) { gym, sessions, outdoor ->
                Triple(gym, sessions, outdoor)
            }.collectLatest { (gym, sessions, outdoor) ->
                _uiState.update { state ->
                    state.copy(
                        gymExercises = gym,
                        workoutSessions = sessions,
                        outdoorSessions = outdoor
                    )
                }
            }
        }
    }

    fun onFilterSelected(filter: HistoryFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun exportHistoryToPdf(context: Context) {
        val state = _uiState.value
        val filter = state.selectedFilter
        val filteredGym = filterData(state.gymExercises, filter) { it.date }
        val filteredSessions = filterData(state.workoutSessions, filter) { it.date }
        val filteredOutdoor = filterData(state.outdoorSessions, filter) { it.date }

        if (filteredGym.isEmpty() && filteredSessions.isEmpty() && filteredOutdoor.isEmpty()) {
            android.widget.Toast.makeText(context, "No hay datos para exportar", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val totalCalories = filteredGym.sumOf { it.totalCalories } + filteredSessions.sumOf { it.calories } + filteredOutdoor.sumOf { it.calories }
        val totalSteps = filteredSessions.sumOf { it.steps } + filteredOutdoor.sumOf { it.steps ?: 0 }
        val totalDistance = filteredSessions.sumOf { it.distance.toDouble() } + filteredOutdoor.sumOf { it.distance.toDouble() }
        val totalTimeSeconds = filteredGym.sumOf { it.elapsedTime } + filteredSessions.sumOf { it.time.time / 1000 } + filteredOutdoor.sumOf { it.time / 1000 }

        val filterName = when (filter) {
            HistoryFilter.TODAY -> "Hoy"
            HistoryFilter.LAST_WEEK -> "Última Semana"
            HistoryFilter.LAST_MONTH -> "Último Mes"
            HistoryFilter.ALL -> "Todo el Historial"
        }

        viewModelScope.launch(Dispatchers.IO) {
            PdfExporter.generateHistoryPdf(
                context = context,
                filterName = filterName,
                gymExercises = filteredGym,
                workoutSessions = filteredSessions,
                outdoorSessions = filteredOutdoor,
                units = state.userData?.unitsConfig ?: UnitsConfig.METRIC,
                totalCalories = totalCalories,
                totalSteps = totalSteps,
                totalDistanceKm = totalDistance,
                totalTime = DateUtils.formatElapsedTime(totalTimeSeconds)
            )
        }
    }

    private fun <T> filterData(data: List<T>, filter: HistoryFilter, dateSelector: (T) -> Long): List<T> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return when (filter) {
            HistoryFilter.TODAY -> data.filter { dateSelector(it) >= cal.timeInMillis }
            HistoryFilter.LAST_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) cal.add(Calendar.DAY_OF_YEAR, -7)
                data.filter { dateSelector(it) >= cal.timeInMillis }
            }
            HistoryFilter.LAST_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                data.filter { dateSelector(it) >= cal.timeInMillis }
            }
            HistoryFilter.ALL -> data
        }
    }

    private fun initStepCounter() {
        if (!stepCounterSensor.isSensorAvailable()) return
        viewModelScope.launch {
            combine(
                stepCounterSensor.steps,
                stepCounterDataStore.stepOffset
            ) { totalStepsSinceReboot, offset ->
                (totalStepsSinceReboot - offset).coerceAtLeast(0)
            }.collect { dailySteps ->
                _uiState.update { it.copy(dailySteps = dailySteps) }
                checkStreak(dailySteps)
            }
        }
    }

    private fun checkStreak(dailySteps: Int) {
        val profile = _uiState.value.userProfile ?: return
        if (isUpdatingStreak) return // Evitar ejecuciones simultáneas
        
        val goal = profile.dailyStepsGoal
        if (dailySteps >= goal && goal > 0) {
            val today = Calendar.getInstance()
            val lastDate = Calendar.getInstance().apply { timeInMillis = profile.lastStreakDate }
            
            // Si es un día nuevo para la racha
            if (profile.lastStreakDate == 0L || !isSameDay(today, lastDate)) {
                isUpdatingStreak = true
                
                val newStreak = if (isYesterday(today, lastDate)) {
                    profile.currentStreak + 1
                } else {
                    1
                }
                
                // Actualizamos TODO de una vez: racha, fecha y marca de "mostrado"
                // Al marcar 'lastStreakShown = newStreak' aquí mismo, evitamos que el Flow
                // de getUserProfile intente disparar la animación otra vez.
                val updatedProfile = profile.copy(
                    currentStreak = newStreak,
                    lastStreakDate = System.currentTimeMillis(),
                    lastStreakShown = newStreak 
                )
                
                viewModelScope.launch {
                    // 1. Persistencia local
                    userProfileUseCase.updateUserProfileUseCase(updatedProfile)
                    
                    // 2. Disparo manual de la animación (solo una vez)
                    _uiState.update { it.copy(
                        showLevelUp = true,
                        streakLevel = newStreak
                    )}
                    
                    // 3. Sincronización remota
                    _uiState.value.authUser?.let { user ->
                        syncUserDataUseCase.syncProfile(user.id, updatedProfile)
                    }
                    
                    isUpdatingStreak = false
                }
            }
        }
    }
}
