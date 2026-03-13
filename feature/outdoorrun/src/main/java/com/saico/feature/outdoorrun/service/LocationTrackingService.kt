package com.saico.feature.outdoorrun.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.saico.core.ui.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class TrackingState { RUNNING, PAUSED, STOPPED }

@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Métricas para la notificación
    private var startTime = 0L
    private var pausedTimeOffset = 0L
    private var totalDistanceMeters = 0f
    private var lastLocation: Location? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        
        const val NOTIFICATION_ID = 5001
        const val CHANNEL_ID = "location_tracking_channel"

        val locationUpdates = MutableSharedFlow<Location>(extraBufferCapacity = 1)
        val serviceStatus = MutableStateFlow(TrackingState.STOPPED)
    }

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onLocationResult(result: LocationResult) {
            if (serviceStatus.value != TrackingState.RUNNING) return
            result.lastLocation?.let { location ->
                lastLocation?.let { last ->
                    totalDistanceMeters += last.distanceTo(location)
                }
                lastLocation = location
                updateNotification()
                serviceScope.launch {
                    locationUpdates.emit(location)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (serviceStatus.value == TrackingState.STOPPED) {
                    serviceStatus.value = TrackingState.RUNNING
                    startTime = SystemClock.elapsedRealtime()
                    totalDistanceMeters = 0f
                    lastLocation = null
                    startForegroundService()
                }
            }
            ACTION_PAUSE -> {
                if (serviceStatus.value == TrackingState.RUNNING) {
                    serviceStatus.value = TrackingState.PAUSED
                    pausedTimeOffset = SystemClock.elapsedRealtime() - startTime
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    updateNotification()
                }
            }
            ACTION_RESUME -> {
                if (serviceStatus.value == TrackingState.PAUSED) {
                    serviceStatus.value = TrackingState.RUNNING
                    startTime = SystemClock.elapsedRealtime() - pausedTimeOffset
                    requestLocationUpdates()
                    updateNotification()
                }
            }
            ACTION_STOP -> {
                serviceStatus.value = TrackingState.STOPPED
                stopService()
            }
        }
        return START_NOT_STICKY
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @RequiresApi(Build.VERSION_CODES.N)
    private fun startForegroundService() {
        createNotificationChannel()
        val notification = buildNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        requestLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buildNotification(): Notification {
        val appIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(this, 0, appIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Acción Pausar/Reanudar
        val toggleActionIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = if (serviceStatus.value == TrackingState.RUNNING) ACTION_PAUSE else ACTION_RESUME
        }
        val togglePendingIntent = PendingIntent.getService(this, 2, toggleActionIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        
        val toggleLabel = if (serviceStatus.value == TrackingState.RUNNING) getString(R.string.outdoor_notification_pause) else "REANUDAR"
        val toggleIcon = if (serviceStatus.value == TrackingState.RUNNING) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

        val techBlue = 0xFF3FB9F6.toInt()
        val distanceKm = totalDistanceMeters / 1000f

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setColor(techBlue)
            .setColorized(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentTitle(getString(R.string.outdoor_notification_title))
            .setSubText(getString(R.string.outdoor_notification_subtext))
            .setContentText(getString(R.string.outdoor_notification_distance, distanceKm))
            .setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(getString(R.string.outdoor_notification_big_title))
                .bigText(getString(R.string.outdoor_notification_big_text, distanceKm)))
            .addAction(toggleIcon, toggleLabel, togglePendingIntent)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (serviceStatus.value == TrackingState.RUNNING) {
            builder.setUsesChronometer(true)
            builder.setWhen(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - startTime))
        } else {
            builder.setUsesChronometer(false)
            val elapsedMillis = pausedTimeOffset
            val mins = (elapsedMillis / 60000).toInt()
            val secs = (elapsedMillis % 60000 / 1000).toInt()
            builder.setContentText("PAUSADO • ${String.format("%02d:%02d", mins, secs)} • ${String.format("%.2f km", distanceKm)}")
        }

        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateNotification() {
        if (serviceStatus.value == TrackingState.STOPPED) return
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(2f)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null)
        } catch (e: SecurityException) { e.printStackTrace() }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.location_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stopService() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        serviceStatus.value = TrackingState.STOPPED
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
