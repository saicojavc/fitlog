package com.saico.feature.outdoorrun

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.usecase.outdoor.OutdoorUseCase
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.model.LocationPoint
import com.saico.core.model.OutdoorSession
import com.saico.core.model.UserProfile
import com.saico.feature.outdoorrun.model.OutdoorUiState
import com.saico.feature.outdoorrun.service.LocationTrackingService
import com.saico.feature.outdoorrun.service.TrackingState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutdoorRunViewModel @Inject constructor(
    private val outdoorUseCase: OutdoorUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    private val userSettingsDataStore: UserSettingsDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OutdoorUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var lastLocation: Location? = null
    private var pausedTimeOffset: Long = 0L
    private var lastStartTime: Long = 0L

    private var userProfile: UserProfile? = null

    init {
        // Obtener perfil del usuario para el cálculo de calorías
        viewModelScope.launch {
            userProfileUseCase.getUserProfileUseCase().collectLatest { profile ->
                userProfile = profile
            }
        }

        // Observar configuración del usuario
        viewModelScope.launch {
            userSettingsDataStore.userData.collectLatest { data ->
                _uiState.update { it.copy(unitsConfig = data.unitsConfig) }
            }
        }

        // Observar actualizaciones del servicio de forma reactiva
        viewModelScope.launch {
            LocationTrackingService.locationUpdates.collectLatest { location ->
                updateMetrics(location)
            }
        }

        // Sincronizar estado con el servicio (notificación)
        viewModelScope.launch {
            LocationTrackingService.serviceStatus.collectLatest { state ->
                handleServiceStateChange(state)
            }
        }
    }

    private fun handleServiceStateChange(state: TrackingState) {
        when (state) {
            TrackingState.RUNNING -> {
                if (!_uiState.value.isRunning) {
                    _uiState.update { it.copy(isRunning = true) }
                    startTimer()
                }
            }

            TrackingState.PAUSED -> {
                if (_uiState.value.isRunning) {
                    _uiState.update { it.copy(isRunning = false) }
                    timerJob?.cancel()
                    timerJob = null
                    pausedTimeOffset = _uiState.value.timeMillis
                }
            }

            TrackingState.STOPPED -> {
                _uiState.update { it.copy(isRunning = false) }
                timerJob?.cancel()
                timerJob = null
            }
        }
    }

    fun setActivityType(type: String) {
        _uiState.update { it.copy(activityType = type) }
    }

    fun startTracking() {
        if (LocationTrackingService.serviceStatus.value == TrackingState.RUNNING) return

        val intent = Intent(context, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun pauseTracking() {
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeTracking() {
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun stopTracking() {
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun saveSession() {
        viewModelScope.launch {
            val state = _uiState.value
            val session = OutdoorSession(
                activityType = state.activityType,
                steps = if (state.activityType == "outdoor_run") state.steps else null,
                averageSpeed = state.averageSpeed,
                distance = state.distanceMeters / 1000f,
                calories = state.calories,
                time = state.timeMillis,
                date = System.currentTimeMillis(),
                routePath = state.routePath
            )
            outdoorUseCase.saveOutdoorSessionUseCase(session)
            _uiState.update {
                OutdoorUiState(
                    activityType = state.activityType,
                    unitsConfig = state.unitsConfig
                )
            }
            pausedTimeOffset = 0L
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        lastStartTime = System.currentTimeMillis() - _uiState.value.timeMillis
        timerJob = viewModelScope.launch {
            while (true) {
                _uiState.update { it.copy(timeMillis = System.currentTimeMillis() - lastStartTime) }
                delay(1000L)
            }
        }
    }

    private fun updateMetrics(newLocation: Location) {
        val currentPath = _uiState.value.routePath.toMutableList()
        val newPoint = LocationPoint(newLocation.latitude, newLocation.longitude)
        currentPath.add(newPoint)

        var newDistance = _uiState.value.distanceMeters
        var newElevation = _uiState.value.elevationGain

        lastLocation?.let { last ->
            newDistance += last.distanceTo(newLocation)
            if (newLocation.hasAltitude() && last.hasAltitude()) {
                val diff = newLocation.altitude - last.altitude
                if (diff > 0) newElevation += diff.toFloat()
            }
        }

        lastLocation = newLocation

        val avgSpeedKmh = if (_uiState.value.timeMillis > 0) {
            (newDistance / (_uiState.value.timeMillis / 1000f)) * 3.6f
        } else 0f

        // Calcular calorías quemadas
        val calculatedCalories = calculateCalories(
            activityType = _uiState.value.activityType,
            avgSpeedKmh = avgSpeedKmh,
            durationMillis = _uiState.value.timeMillis,
            userProfile = userProfile
        )

        _uiState.update {
            it.copy(
                routePath = currentPath,
                distanceMeters = newDistance,
                currentSpeed = newLocation.speed * 3.6f,
                averageSpeed = avgSpeedKmh,
                elevationGain = newElevation,
                calories = calculatedCalories
            )
        }
    }

    /**
     * Calcula las calorías quemadas usando el método MET (Metabolic Equivalent of Task).
     * Fórmula: Calorías = MET * Peso(kg) * Tiempo(horas)
     */
    private fun calculateCalories(
        activityType: String,
        avgSpeedKmh: Float,
        durationMillis: Long,
        userProfile: UserProfile?
    ): Int {
        val weight = userProfile?.weightKg ?: 70.0 // Peso por defecto si no hay perfil
        val durationHours = durationMillis / 3600000f

        val met = if (activityType == "cycling") {
            // MET para ciclismo basado en velocidad aproximada
            when {
                avgSpeedKmh < 16 -> 4.0
                avgSpeedKmh < 19 -> 6.0
                avgSpeedKmh < 22 -> 8.0
                avgSpeedKmh < 25 -> 10.0
                else -> 12.0
            }
        } else {
            // MET para carrera/caminata basado en velocidad
            when {
                avgSpeedKmh < 5 -> 3.5  // Caminata lenta
                avgSpeedKmh < 6.5 -> 5.0 // Caminata rápida
                avgSpeedKmh < 8 -> 8.3  // Trote suave
                avgSpeedKmh < 10 -> 9.8 // Carrera moderada
                avgSpeedKmh < 12 -> 11.5 // Carrera rápida
                else -> 12.8 // Carrera muy rápida
            }
        }

        // Ajuste por edad (opcional, el MET estándar es para ~35 años)
        // val ageFactor = if (userProfile != null) (100 - userProfile.age) / 100.0 else 1.0

        return (met * weight * durationHours).toInt()
    }
}
