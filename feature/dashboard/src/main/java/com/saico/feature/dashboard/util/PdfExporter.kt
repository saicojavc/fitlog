package com.saico.feature.dashboard.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.GymExercise
import com.saico.core.model.OutdoorSession
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WorkoutSession
import com.saico.core.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.*


object PdfExporter {

    suspend fun generateHistoryPdf(
        context: Context,
        filterName: String,
        gymExercises: List<GymExercise>,
        workoutSessions: List<WorkoutSession>,
        outdoorSessions: List<OutdoorSession>,
        units: UnitsConfig,
        totalCalories: Int,
        totalSteps: Int,
        totalDistanceKm: Double,
        totalTime: String
    ) {
        val pdfDocument = PdfDocument()

        // --- COLORES ---
        val darkBg = 0xFF0D1424.toInt()
        val techBlue = 0xFF3FB9F6.toInt()
        val cardBg = 0xFF1A2333.toInt()
        val gymColor = 0xFFA855F7.toInt()
        val cardioColor = 0xFFFF9F1C.toInt()

        val paint = Paint()
        val titlePaint = Paint().apply {
            textSize = 24f; isFakeBoldText = true; color = techBlue; letterSpacing = 0.05f
        }
        val headerPaint = Paint().apply {
            textSize = 14f; isFakeBoldText = true; color = Color.WHITE
        }
        val textPaint = Paint().apply {
            textSize = 11f; color = Color.WHITE; isAntiAlias = true
        }
        val labelPaint = Paint().apply {
            textSize = 10f; color = techBlue; isFakeBoldText = true; letterSpacing = 0.1f
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        fun preparePage(c: Canvas) {
            c.drawColor(darkBg)
            paint.style = Paint.Style.FILL
            paint.color = techBlue; paint.alpha = 40
            c.drawRect(0f, 0f, 10f, 842f, paint)
        }

        preparePage(canvas)
        var y = 60f

        // 1. HEADER
        val reportTitle = context.getString(R.string.pdf_report_title) + " " + context.getString(R.string.pdf_analytics_suffix)
        canvas.drawText(reportTitle.uppercase(), 40f, y, titlePaint)
        y += 25f
        canvas.drawText("${context.getString(R.string.pdf_filter_label, filterName)} | FITLOG PRO", 40f, y, labelPaint)
        y += 40f

        // 2. DASHBOARD
        paint.color = cardBg; paint.alpha = 255; paint.style = Paint.Style.FILL
        canvas.drawRoundRect(40f, y, 555f, y + 100f, 15f, 15f, paint)
        paint.style = Paint.Style.STROKE; paint.color = techBlue; paint.alpha = 60
        canvas.drawRoundRect(40f, y, 555f, y + 100f, 15f, 15f, paint)
        paint.style = Paint.Style.FILL

        val dashY = y + 35f
        canvas.drawText(context.getString(R.string.pdf_stat_cals_label, totalCalories), 70f, dashY, textPaint)
        canvas.drawText(context.getString(R.string.pdf_stat_steps_label, totalSteps), 300f, dashY, textPaint)
        canvas.drawText(context.getString(R.string.pdf_stat_dist_label, UnitsConverter.formatDistance(totalDistanceKm, units)), 70f, dashY + 35f, textPaint)
        canvas.drawText(context.getString(R.string.pdf_stat_time_label, totalTime), 300f, dashY + 35f, textPaint)

        y += 145f
        canvas.drawText(context.getString(R.string.pdf_activities_detail).uppercase(), 40f, y, headerPaint)
        y += 45f

        // 3. PROCESAMIENTO DE DATOS
        val combinedItems = (
                gymExercises.map { Triple("GYM", it.date, it) } +
                        workoutSessions.map { Triple("CARDIO", it.date, it) } +
                        outdoorSessions.map { Triple("OUTDOOR", it.date, it) }
                ).sortedByDescending { it.second }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        combinedItems.forEach { (type, _, data) ->
            if (y > 700f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas; preparePage(canvas); y = 60f
            }

            paint.style = Paint.Style.FILL; paint.color = cardBg; paint.alpha = 180
            canvas.drawRoundRect(40f, y - 20f, 555f, y + 50f, 10f, 10f, paint)

            val accentColor = when(type) {
                "GYM" -> gymColor
                "CARDIO" -> cardioColor
                else -> techBlue
            }
            paint.color = accentColor; paint.alpha = 255
            canvas.drawRect(40f, y - 20f, 45f, y + 50f, paint)

            when (type) {
                "GYM" -> {
                    val gym = data as GymExercise
                    canvas.drawText(context.getString(R.string.pdf_gym_label, dateFormat.format(Date(gym.date))).uppercase(), 60f, y, labelPaint.apply { color = accentColor })
                    y += 22f
                    canvas.drawText(context.getString(R.string.pdf_activity_summary_gym, gym.totalCalories, formatElapsedTime(gym.elapsedTime)), 60f, y, textPaint)
                }
                "CARDIO" -> {
                    val session = data as WorkoutSession
                    canvas.drawText(context.getString(R.string.pdf_cardio_label, dateFormat.format(Date(session.date))).uppercase(), 60f, y, labelPaint.apply { color = accentColor })
                    y += 22f
                    canvas.drawText(context.getString(R.string.pdf_activity_summary_cardio, session.steps, UnitsConverter.formatDistance(session.distance.toDouble(), units), session.calories), 60f, y, textPaint)
                }
                "OUTDOOR" -> {
                    val outdoor = data as OutdoorSession
                    val actName = if (outdoor.activityType == "cycling") context.getString(R.string.cycling) else context.getString(R.string.outdoor_run)
                    canvas.drawText("$actName - ${dateFormat.format(Date(outdoor.date))}".uppercase(), 60f, y, labelPaint.apply { color = accentColor })
                    y += 22f

                    val distStr = UnitsConverter.formatDistance(outdoor.distance.toDouble(), units)
                    val timeStr = formatElapsedTime(outdoor.time / 1000)
                    val speed = if (units == UnitsConfig.IMPERIAL) String.format("%.1f mph", outdoor.averageSpeed * 0.621371f) else String.format("%.1f km/h", outdoor.averageSpeed)
                    canvas.drawText("Dist: $distStr | Time: $timeStr | Avg Speed: $speed", 60f, y, textPaint)
                }
            }
            y += 75f
        }

        // 4. GRÁFICO (Corregido: Uso correcto de Canvas.drawPath)
        y += 30f
        if (y < 650f) {
            canvas.drawText("CALORIES PROGRESS (GYM & CARDIO)", 40f, y, headerPaint)
            y += 40f
            val graphHeight = 100f
            val graphWidth = 460f
            val startX = 65f
            val startY = y + graphHeight

            val sessionsWithCals = combinedItems.filter { it.first == "GYM" || it.first == "CARDIO" }.take(10).reversed()

            if (sessionsWithCals.size > 1) {
                val maxCals = sessionsWithCals.maxOf {
                    if (it.first == "GYM") (it.third as GymExercise).totalCalories.toFloat()
                    else (it.third as WorkoutSession).calories.toFloat()
                }.coerceAtLeast(100f)

                val stepX = graphWidth / (sessionsWithCals.size - 1)
                val chartPath = Path() // Definimos el objeto Path

                sessionsWithCals.forEachIndexed { i, item ->
                    val cals = if (item.first == "GYM") (item.third as GymExercise).totalCalories.toFloat()
                    else (item.third as WorkoutSession).calories.toFloat()
                    val px = startX + (i * stepX)
                    val py = startY - (cals / maxCals * graphHeight)

                    if (i == 0) chartPath.moveTo(px, py) else chartPath.lineTo(px, py)

                    // Dibujamos los puntos directamente al canvas
                    paint.style = Paint.Style.FILL
                    paint.color = techBlue
                    canvas.drawCircle(px, py, 3f, paint)
                }

                // Ahora dibujamos el camino (Path) completo al canvas
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                paint.color = techBlue
                canvas.drawPath(chartPath, paint) // CORRECCIÓN: Se pasa el objeto Path
            }
        }

        pdfDocument.finishPage(page)
        saveFinalPdf(context, pdfDocument)
    }

    private suspend fun saveFinalPdf(context: Context, document: PdfDocument) {
        val fileName = "FitLog_History_${System.currentTimeMillis()}.pdf"
        try {
            withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    uri?.let { context.contentResolver.openOutputStream(it).use { out -> document.writeTo(out) } }
                } else {
                    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                    document.writeTo(FileOutputStream(file))
                }
            }
            withContext(Dispatchers.Main) { Toast.makeText(context, context.getString(R.string.pdf_success), Toast.LENGTH_LONG).show() }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { Toast.makeText(context, context.getString(R.string.pdf_error, e.message), Toast.LENGTH_LONG).show() }
        } finally {
            document.close()
        }
    }

    private fun formatElapsedTime(seconds: Long): String {
        val h = seconds / 3600; val m = (seconds % 3600) / 60; val s = seconds % 60
        return if (h > 0) "${h}h ${m}m ${s}s" else "${m}m ${s}s"
    }
}