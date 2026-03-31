package com.saico.feature.gymwork.util

import com.saico.core.ui.R
import com.saico.feature.gymwork.state.GuidedExerciseItem
import java.util.Calendar

object GuidedWorkoutProvider {

    fun getRoutineForToday(): List<GuidedExerciseItem> {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> getMondayRoutine()
            Calendar.TUESDAY -> getTuesdayRoutine()
            Calendar.WEDNESDAY -> getWednesdayRoutine()
            Calendar.THURSDAY -> getThursdayRoutine()
            Calendar.FRIDAY -> getFridayRoutine()
            else -> emptyList() 
        }
    }

    fun getDayNameRes(): Int {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> R.string.monday_title
            Calendar.TUESDAY -> R.string.tuesday_title
            Calendar.WEDNESDAY -> R.string.wednesday_title
            Calendar.THURSDAY -> R.string.thursday_title
            Calendar.FRIDAY -> R.string.friday_title
            else -> R.string.rest_day_title
        }
    }

    private fun getMondayRoutine() = listOf(
        GuidedExerciseItem(R.string.ex_flat_bench, "4", "12", R.string.desc_flat_bench, R.string.cat_chest),
        GuidedExerciseItem(R.string.ex_incline_bench, "3", "12", R.string.desc_incline_bench, R.string.cat_chest),
        GuidedExerciseItem(R.string.ex_shoulder_press, "3", "12", R.string.desc_shoulder_press, R.string.cat_shoulders),
        GuidedExerciseItem(R.string.ex_lateral_raises, "3", "15", R.string.desc_lateral_raises, R.string.cat_shoulders),
        GuidedExerciseItem(R.string.ex_bench_dips, "3", "12-15", R.string.desc_bench_dips, R.string.cat_triceps),
        GuidedExerciseItem(R.string.ex_treadmill, "1", "15-20 min", R.string.desc_treadmill_const, R.string.cat_cardio)
    )

    private fun getTuesdayRoutine() = listOf(
        GuidedExerciseItem(R.string.ex_leg_extension, "4", "15", R.string.desc_leg_extension, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_leg_curl, "4", "12", R.string.desc_leg_curl, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_goblet_squat, "4", "20", R.string.desc_goblet_squat, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_lunges, "3", "12", R.string.desc_lunges, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_calf_raises, "4", "20", R.string.desc_calf_raises, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_treadmill, "1", "10 min", R.string.desc_treadmill_hiit, R.string.cat_cardio)
    )

    private fun getWednesdayRoutine() = listOf(
        GuidedExerciseItem(R.string.ex_lat_pulldown, "4", "12", R.string.desc_lat_pulldown, R.string.cat_back),
        GuidedExerciseItem(R.string.ex_seated_row, "4", "12", R.string.desc_seated_row, R.string.cat_back),
        GuidedExerciseItem(R.string.ex_pulley_biceps, "3", "12", R.string.desc_pulley_biceps, R.string.cat_biceps),
        GuidedExerciseItem(R.string.ex_hammer_curl, "3", "12", R.string.desc_hammer_curl, R.string.cat_biceps),
        GuidedExerciseItem(R.string.ex_concentration_curl, "3", "12", R.string.desc_concentration_curl, R.string.cat_biceps),
        GuidedExerciseItem(R.string.ex_treadmill, "1", "15-20 min", R.string.desc_treadmill_fast, R.string.cat_cardio)
    )

    private fun getThursdayRoutine() = listOf(
        GuidedExerciseItem(R.string.ex_decline_bench, "3", "12", R.string.desc_decline_bench, R.string.cat_chest),
        GuidedExerciseItem(R.string.ex_one_arm_row, "3", "12", R.string.desc_one_arm_row, R.string.cat_back),
        GuidedExerciseItem(R.string.ex_db_bench_press, "3", "Fallo", R.string.desc_db_bench_press, R.string.cat_chest),
        GuidedExerciseItem(R.string.ex_rope_pushdown, "3", "12", R.string.desc_rope_pushdown, R.string.cat_triceps),
        GuidedExerciseItem(R.string.ex_face_pulls, "3", "15", R.string.desc_face_pulls, R.string.cat_shoulders),
        GuidedExerciseItem(R.string.ex_treadmill, "1", "15 min", R.string.desc_treadmill_burn, R.string.cat_cardio)
    )

    private fun getFridayRoutine() = listOf(
        GuidedExerciseItem(R.string.ex_rdl, "4", "15", R.string.desc_rdl, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_leg_extension, "3", "20", R.string.desc_leg_extension_pump, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_side_lunges, "3", "12", R.string.desc_side_lunges, R.string.cat_legs),
        GuidedExerciseItem(R.string.ex_plank, "3", "45-60", R.string.desc_plank, R.string.cat_core),
        GuidedExerciseItem(R.string.ex_leg_raises, "3", "15", R.string.desc_leg_raises, R.string.cat_core),
        GuidedExerciseItem(R.string.ex_treadmill, "1", "20-25 min", R.string.desc_treadmill_final, R.string.cat_cardio)
    )
}
