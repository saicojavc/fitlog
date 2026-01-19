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
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WorkoutSession
import com.saico.core.ui.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    suspend fun generateHistoryPdf(
        context: Context,
        filterName: String,
        gymExercises: List<GymExercise>,
        workoutSessions: List<WorkoutSession>,
        units: UnitsConfig,
        totalCalories: Int,
        totalSteps: Int,
        totalDistanceKm: Double,
        totalTime: String
    ) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
            color = Color.BLACK
        }
        val headerPaint = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
            color = Color.DKGRAY
        }
        val textPaint = Paint().apply {
            textSize = 12f
            color = Color.BLACK
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var y = 40f

        // 1. Título y Filtro
        canvas.drawText(context.getString(R.string.pdf_report_title), 40f, y, titlePaint)
        y += 25f
        canvas.drawText(context.getString(R.string.pdf_filter_label, filterName), 40f, y, textPaint)
        y += 20f
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText(context.getString(R.string.pdf_generation_date, dateStr), 40f, y, textPaint)
        
        y += 30f
        canvas.drawLine(40f, y, 555f, y, paint)
        y += 30f

        // 2. Resumen del Periodo (CON UNIDADES CONVERTIDAS)
        canvas.drawText(context.getString(R.string.pdf_summary_title), 40f, y, headerPaint)
        y += 25f
        canvas.drawText(context.getString(R.string.pdf_total_calories, totalCalories), 60f, y, textPaint)
        y += 20f
        canvas.drawText(context.getString(R.string.pdf_total_steps, totalSteps), 60f, y, textPaint)
        y += 20f
        val formattedTotalDist = UnitsConverter.formatDistance(totalDistanceKm, units)
        canvas.drawText(context.getString(R.string.pdf_total_distance, formattedTotalDist), 60f, y, textPaint)
        y += 20f
        canvas.drawText(context.getString(R.string.pdf_active_time, totalTime), 60f, y, textPaint)
        y += 20f
        canvas.drawText(context.getString(R.string.pdf_total_sessions, gymExercises.size + workoutSessions.size), 60f, y, textPaint)
        
        y += 40f
        canvas.drawText(context.getString(R.string.pdf_activities_detail), 40f, y, headerPaint)
        y += 10f
        canvas.drawLine(40f, y, 180f, y, paint)
        y += 30f

        val combinedItems = (gymExercises.map { "GYM" to it.date } + workoutSessions.map { "CARDIO" to it.date })
            .sortedByDescending { it.second }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        combinedItems.forEach { item ->
            if (y > 780f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }

            if (item.first == "GYM") {
                val gym = gymExercises.find { it.date == item.second } ?: return@forEach
                val gymTitle = context.getString(R.string.pdf_gym_workout, dateFormat.format(Date(gym.date)))
                canvas.drawText(gymTitle, 40f, y, textPaint.apply { isFakeBoldText = true })
                y += 18f
                val gymSum = context.getString(R.string.pdf_activity_summary_gym, gym.totalCalories, formatElapsedTime(gym.elapsedTime))
                canvas.drawText("   $gymSum", 40f, y, textPaint.apply { isFakeBoldText = false })
                y += 15f
                gym.exercises.take(5).forEach { ex ->
                    canvas.drawText("   - ${ex.name}: ${ex.sets}x${ex.reps} (${UnitsConverter.formatWeight(ex.weightKg, units)})", 50f, y, textPaint)
                    y += 15f
                }
            } else {
                val session = workoutSessions.find { it.date == item.second } ?: return@forEach
                val cardioTitle = context.getString(R.string.pdf_cardio_session, dateFormat.format(Date(session.date)))
                canvas.drawText(cardioTitle, 40f, y, textPaint.apply { isFakeBoldText = true })
                y += 18f
                val dist = UnitsConverter.formatDistance(session.distance.toDouble(), units)
                val cardioSum = context.getString(R.string.pdf_activity_summary_cardio, session.steps, dist, session.calories)
                canvas.drawText("   $cardioSum", 40f, y, textPaint.apply { isFakeBoldText = false })
                y += 15f
            }
            y += 15f
        }

        pdfDocument.finishPage(page)

        val fileName = "FitLog_History_${System.currentTimeMillis()}.pdf"
        try {
            saveFile(context, pdfDocument, fileName)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.pdf_success), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.pdf_error, e.message), Toast.LENGTH_LONG).show()
            }
        } finally {
            pdfDocument.close()
        }
    }

    private fun saveFile(context: Context, document: PdfDocument, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it).use { outputStream ->
                    document.writeTo(outputStream)
                }
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            document.writeTo(FileOutputStream(file))
        }
    }

    private fun formatElapsedTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "${h}h ${m}m ${s}s" else "${m}m ${s}s"
    }
}
