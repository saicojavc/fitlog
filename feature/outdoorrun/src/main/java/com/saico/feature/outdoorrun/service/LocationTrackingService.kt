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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isTracking = false

    // Métricas para la notificación
    private var startTime = 0L
    private var totalDistanceMeters = 0f
    private var lastLocation: Location? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NOTIFICATION_ID = 5001
        const val CHANNEL_ID = "location_tracking_channel"

        val locationUpdates = MutableSharedFlow<Location>(extraBufferCapacity = 1)
    }

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onLocationResult(result: LocationResult) {
            if (!isTracking) return
            result.lastLocation?.let { location ->
                // Actualizar métricas internas
                lastLocation?.let { last ->
                    totalDistanceMeters += last.distanceTo(location)
                }
                lastLocation = location

                // Actualizar notificación con datos nuevos
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
                if (!isTracking) {
                    isTracking = true
                    startTime = SystemClock.elapsedRealtime()
                    totalDistanceMeters = 0f
                    lastLocation = null
                    startForegroundService()
                }
            }

            ACTION_STOP -> {
                isTracking = false
                stopService()
            }
        }
        return START_NOT_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startForegroundService() {
        createNotificationChannel()
        val notification = buildNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        requestLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buildNotification(): Notification {
        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val techBlue = 0xFF3FB9F6.toInt()
        val distanceKm = totalDistanceMeters / 1000f

        // Usamos el cronómetro nativo para impacto visual en tiempo real
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation) 
            .setPriority(NotificationCompat.PRIORITY_MAX) 
            .setCategory(NotificationCompat.CATEGORY_WORKOUT) 
            .setColor(techBlue)
            .setColorized(true) 
            .setOngoing(true)
            .setOnlyAlertOnce(true) 

            .setContentTitle(getString(R.string.outdoor_notification_title))
            .setSubText(getString(R.string.outdoor_notification_subtext))

            .setUsesChronometer(true)
            .setChronometerCountDown(false)
            .setWhen(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - startTime))

            .setContentText(getString(R.string.outdoor_notification_distance, distanceKm))

            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(getString(R.string.outdoor_notification_big_title))
                    .bigText(getString(R.string.outdoor_notification_big_text, distanceKm))
            )

            .addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.outdoor_notification_pause), pendingIntent)
            .addAction(android.R.drawable.ic_menu_directions, getString(R.string.outdoor_notification_view_map), pendingIntent)

            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateNotification() {
        if (!isTracking) return
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(2f)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun stopService() {
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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

    override fun onDestroy() {
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
