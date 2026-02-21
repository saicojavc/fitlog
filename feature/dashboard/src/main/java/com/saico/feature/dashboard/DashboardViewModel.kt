package com.saico.feature.dashboard

import android.content.Context
import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.common.util.StepCounterSensor
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.usecase.gym_exercise.GymUseCase
import com.saico.core.domain.usecase.workout.WorkoutUseCase
import com.saico.core.domain.usecase.SyncUserDataUseCase
import com.saico.core.model.UserProfile
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WeightEntry
import com.saico.core.model.WorkoutSession
import com.saico.core.network.usecase.LoginWithGoogleUseCase
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
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase,
    private val workoutUseCase: WorkoutUseCase,
    private val gymUseCase: GymUseCase,
    private val stepCounterSensor: StepCounterSensor,
    private val stepCounterDataStore: StepCounterDataStore,
    private val userDataStore: UserSettingsDataStore,
    private val notificationHelper: NotificationHelper,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val authRepository: AuthRepository,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getUserProfile()
        initStepCounter()
        getWeeklyWorkouts()
        getHistoryData()
        getUserData()
        checkAppVersion()
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        authRepository.getCurrentUser()?.let { user ->
            _uiState.update { it.copy(authUser = user) }
            syncData(user.id)
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingLogin = true) }
            loginWithGoogleUseCase(idToken).onSuccess { user ->
                _uiState.update { it.copy(isLoadingLogin = false, authUser = user) }
                syncData(user.id)
            }.onFailure {
                _uiState.update { it.copy(isLoadingLogin = false) }
            }
        }
    }

    private fun syncData(uid: String) {
        viewModelScope.launch { syncUserDataUseCase.syncAll(uid) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(authUser = null) }
        }
    }

    private fun checkAppVersion() {
        firebaseDatabase.getReference("version").addValueEventListener(object : ValueEventListener {
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
                _uiState.update { it.copy(userProfile = profile) }
                _uiState.value.authUser?.let { user ->
                    profile?.let { syncUserDataUseCase.syncProfile(user.id, it) }
                }
            }
        }
    }

    fun updateUserProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
            val currentProfile = _uiState.value.userProfile
            val finalProfile = if (currentProfile != null && currentProfile.weightKg != updatedProfile.weightKg) {
                val newWeightEntry = WeightEntry(weight = updatedProfile.weightKg, date = System.currentTimeMillis())
                updatedProfile.copy(weightHistory = updatedProfile.weightHistory + newWeightEntry)
            } else updatedProfile
            userProfileUseCase.updateUserProfileUseCase(finalProfile)
        }
    }

    private fun getWeeklyWorkouts() {
        viewModelScope.launch {
            workoutUseCase.getWeeklyWorkoutsUseCase().collectLatest { workouts ->
                _uiState.update { it.copy(weeklyWorkouts = workouts) }
                _uiState.value.authUser?.let { user ->
                    workouts.forEach { syncUserDataUseCase.syncWorkout(user.id, it) }
                }
            }
        }
    }

    private fun getHistoryData() {
        viewModelScope.launch {
            combine(gymUseCase.getGymExercisesUseCase(), workoutUseCase.getWorkoutSessionsUseCase()) { gym, sessions ->
                Pair(gym, sessions)
            }.collectLatest { (gym, sessions) ->
                _uiState.update { it.copy(gymExercises = gym, workoutSessions = sessions) }
                _uiState.value.authUser?.let { user ->
                    gym.forEach { syncUserDataUseCase.syncGymExercise(user.id, it) }
                    sessions.forEach { syncUserDataUseCase.syncSession(user.id, it) }
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
        val fGym = filterData(state.gymExercises, filter) { it.date }
        val fSessions = filterData(state.workoutSessions, filter) { it.date }

        if (fGym.isEmpty() && fSessions.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            PdfExporter.generateHistoryPdf(
                context = context,
                filterName = filter.name,
                gymExercises = fGym,
                workoutSessions = fSessions,
                units = state.userData?.unitsConfig ?: UnitsConfig.METRIC,
                totalCalories = fGym.sumOf { it.totalCalories } + fSessions.sumOf { it.calories },
                totalSteps = fSessions.sumOf { it.steps },
                totalDistanceKm = fSessions.sumOf { it.distance.toDouble() },
                totalTime = DateUtils.formatElapsedTime(fGym.sumOf { it.elapsedTime } + fSessions.sumOf { it.time.time / 1000 })
            )
        }
    }

    private fun <T> filterData(data: List<T>, filter: HistoryFilter, dateSelector: (T) -> Long): List<T> {
        val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        return when (filter) {
            HistoryFilter.TODAY -> data.filter { dateSelector(it) >= cal.timeInMillis }
            HistoryFilter.LAST_WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
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
            combine(stepCounterSensor.steps, stepCounterDataStore.stepOffset) { total, offset -> (total - offset).coerceAtLeast(0) }
            .collect { dailySteps -> _uiState.update { it.copy(dailySteps = dailySteps) } }
        }
    }
}
