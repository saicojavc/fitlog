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

        // --- COLORES ESTÉTICA FITLOG ---
        val colorDarkBg = Color.rgb(13, 20, 36)      // Fondo Oscuro #0D1424
        val colorTechBlue = Color.rgb(63, 185, 246)  // Azul Neón #3FB9F6
        val colorWhite = Color.WHITE
        val colorGray = Color.rgb(160, 160, 160)
        val colorCard = Color.rgb(30, 40, 60)       // Azul Grisáceo para tarjetas

        // --- PAINTS ---
        val bgPaint = Paint().apply { color = colorDarkBg }
        val cardPaint = Paint().apply { color = colorCard }
        val accentPaint = Paint().apply {
            color = colorTechBlue
            strokeWidth = 2f
        }
        val titlePaint = Paint().apply {
            textSize = 22f
            isFakeBoldText = true
            color = colorTechBlue
            letterSpacing = 0.05f
        }
        val headerPaint = Paint().apply {
            textSize = 15f
            isFakeBoldText = true
            color = colorWhite
        }
        val textPaint = Paint().apply {
            textSize = 11f
            color = colorWhite
            isAntiAlias = true
        }
        val subTextPaint = Paint().apply {
            textSize = 10f
            color = colorGray
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Función local para pintar fondo en cada página nueva
        fun drawPageBackground(c: Canvas) {
            c.drawRect(0f, 0f, 595f, 842f, bgPaint)
            // Detalle estético: Línea neón superior
            c.drawRect(0f, 0f, 595f, 5f, accentPaint)
        }

        drawPageBackground(canvas)
        var y = 50f

        // 1. TÍTULO Y CABECERA
        canvas.drawText(context.getString(R.string.pdf_report_title).uppercase(), 40f, y, titlePaint)
        y += 25f
        canvas.drawText("${context.getString(R.string.pdf_filter_label, filterName)} | Fitlog Analytics", 40f, y, subTextPaint)

        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText(dateStr, 555f - subTextPaint.measureText(dateStr), y, subTextPaint)

        y += 40f

        // 2. RESUMEN (TARJETA TÉCNICA)
        canvas.drawRoundRect(40f, y, 555f, y + 110f, 15f, 15f, cardPaint)
        canvas.drawText(context.getString(R.string.pdf_summary_title).uppercase(), 60f, y + 30f, headerPaint)

        // Dibujamos las métricas en un grid de 2x2 dentro de la tarjeta
        val gridY = y + 55f
        canvas.drawText("🔥 CALS: $totalCalories", 60f, gridY, textPaint)
        canvas.drawText("👣 STEPS: $totalSteps", 280f, gridY, textPaint)
        canvas.drawText("📏 DIST: ${UnitsConverter.formatDistance(totalDistanceKm, units)}", 60f, gridY + 25f, textPaint)
        canvas.drawText("⏱️ TIME: $totalTime", 280f, gridY + 25f, textPaint)

        y += 150f
        canvas.drawText(context.getString(R.string.pdf_activities_detail).uppercase(), 40f, y, headerPaint)
        canvas.drawLine(40f, y + 8f, 120f, y + 8f, accentPaint)
        y += 40f

        val combinedItems = (
                gymExercises.map { Triple("GYM", it.date, it) } +
                        workoutSessions.map { Triple("CARDIO", it.date, it) } +
                        outdoorSessions.map { Triple("OUTDOOR", it.date, it) }
                ).sortedByDescending { it.second }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        combinedItems.forEach { (type, date, data) ->
            // Verificar espacio en página (Item alto de 70f aprox)
            if (y > 750f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                drawPageBackground(canvas)
                y = 50f
            }

            // Fondo del Item (Glassmorphism sutil)
            canvas.drawRoundRect(40f, y - 15f, 555f, y + 50f, 10f, 10f, Paint().apply {
                color = colorWhite; alpha = 15
            })

            // Barra lateral de color según actividad
            val activityColor = when(type) {
                "GYM" -> Color.rgb(168, 85, 247) // Morado neón
                "CARDIO" -> Color.rgb(255, 159, 28) // Naranja neón
                else -> colorTechBlue
            }
            canvas.drawRect(40f, y - 15f, 44f, y + 50f, Paint().apply { color = activityColor })

            when (type) {
                "GYM" -> {
                    val gym = data as GymExercise
                    canvas.drawText("GYM - ${dateFormat.format(Date(gym.date))}", 60f, y + 5f, textPaint.apply { isFakeBoldText = true })
                    val gymSum = context.getString(R.string.pdf_activity_summary_gym, gym.totalCalories, formatElapsedTime(gym.elapsedTime))
                    canvas.drawText(gymSum, 60f, y + 25f, subTextPaint)
                }
                "CARDIO" -> {
                    val session = data as WorkoutSession
                    canvas.drawText("CARDIO - ${dateFormat.format(Date(session.date))}", 60f, y + 5f, textPaint.apply { isFakeBoldText = true })
                    val dist = UnitsConverter.formatDistance(session.distance.toDouble(), units)
                    val cardioSum = context.getString(R.string.pdf_activity_summary_cardio, session.steps, dist, session.calories)
                    canvas.drawText(cardioSum, 60f, y + 25f, subTextPaint)
                }
                "OUTDOOR" -> {
                    val outdoor = data as OutdoorSession
                    val activityName = if (outdoor.activityType == "cycling") "CYCLING" else "OUTDOOR RUN"
                    canvas.drawText("$activityName - ${dateFormat.format(Date(outdoor.date))}", 60f, y + 5f, textPaint.apply { isFakeBoldText = true })

                    val dist = UnitsConverter.formatDistance(outdoor.distance.toDouble(), units)
                    val time = formatElapsedTime(outdoor.time / 1000)
                    val speed = if (units == UnitsConfig.IMPERIAL) String.format("%.1f mph", outdoor.averageSpeed * 0.621371f) else String.format("%.1f km/h", outdoor.averageSpeed)

                    canvas.drawText("Dist: $dist | Time: $time | Speed: $speed", 60f, y + 25f, subTextPaint)
                }
            }
            y += 80f // Salto de línea entre items
        }

        pdfDocument.finishPage(page)

        // --- PROCESO DE GUARDADO (Tú código original) ---
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