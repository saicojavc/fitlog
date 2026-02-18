package com.saico.core.database.util

import androidx.room.TypeConverter
import com.saico.core.model.GymExerciseItem
import com.saico.core.model.WeightEntry
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.sql.Time

class FitlogTypeConverters {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val gymExerciseListType = Types.newParameterizedType(List::class.java, GymExerciseItem::class.java)
    private val gymExerciseAdapter = moshi.adapter<List<GymExerciseItem>>(gymExerciseListType)

    private val weightEntryListType = Types.newParameterizedType(List::class.java, WeightEntry::class.java)
    private val weightEntryAdapter = moshi.adapter<List<WeightEntry>>(weightEntryListType)

    @TypeConverter
    fun fromTime(time: Time?): Long? {
        return time?.time
    }

    @TypeConverter
    fun toTime(time: Long?): Time? {
        return time?.let { Time(it) }
    }

    @TypeConverter
    fun fromGymExerciseItemList(value: List<GymExerciseItem>?): String? {
        return gymExerciseAdapter.toJson(value)
    }

    @TypeConverter
    fun toGymExerciseItemList(value: String?): List<GymExerciseItem>? {
        return value?.let { gymExerciseAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromWeightHistory(value: List<WeightEntry>?): String? {
        return weightEntryAdapter.toJson(value)
    }

    @TypeConverter
    fun toWeightHistory(value: String?): List<WeightEntry>? {
        return value?.let { weightEntryAdapter.fromJson(it) }
    }
}
