package com.saico.feature.outdoorrun

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.saico.core.domain.usecase.outdoor.OutdoorUseCase
import com.saico.core.model.LocationPoint
import com.saico.core.model.OutdoorSession
import com.saico.feature.outdoorrun.model.OutdoorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutdoorRunViewModel @Inject constructor(
    private val outdoorUseCase: OutdoorUseCase,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(OutdoorUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var lastLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                updateMetrics(location)
            }
        }
    }

    fun setActivityType(type: String) {
        _uiState.update { it.copy(activityType = type) }
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (_uiState.value.isRunning) return

        _uiState.update { it.copy(isRunning = true) }
        startTimer()

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(2f)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun stopTracking() {
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun saveSession() {
        viewModelScope.launch {
            val state = _uiState.value
            val session = OutdoorSession(
                activityType = state.activityType,
                steps = if (state.activityType == "outdoor_run") state.steps else null,
                averageSpeed = state.averageSpeed,
                distance = state.distanceMeters / 1000f, // Convert to Km
                elevation = state.elevationGain,
                time = state.timeMillis,
                date = System.currentTimeMillis(),
                routePath = state.routePath
            )
            outdoorUseCase.saveOutdoorSessionUseCase(session)
            // Reset UI State after saving
            _uiState.update { OutdoorUiState(activityType = state.activityType) }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - _uiState.value.timeMillis
            while (true) {
                _uiState.update { it.copy(timeMillis = System.currentTimeMillis() - startTime) }
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
            // Calcular distancia
            newDistance += last.distanceTo(newLocation)
            
            // Calcular elevación (solo si sube)
            if (newLocation.hasAltitude() && last.hasAltitude()) {
                val diff = newLocation.altitude - last.altitude
                if (diff > 0) newElevation += diff.toFloat()
            }
        }

        lastLocation = newLocation

        val avgSpeed = if (_uiState.value.timeMillis > 0) {
            (newDistance / (_uiState.value.timeMillis / 1000f)) * 3.6f // m/s to km/h
        } else 0f

        _uiState.update {
            it.copy(
                routePath = currentPath,
                distanceMeters = newDistance,
                currentSpeed = newLocation.speed * 3.6f,
                averageSpeed = avgSpeed,
                elevationGain = newElevation
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
