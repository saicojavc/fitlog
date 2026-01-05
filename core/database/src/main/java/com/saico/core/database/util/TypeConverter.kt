package com.saico.core.database.util

import androidx.room.TypeConverter
import java.sql.Time

class TypeConverter {

    @TypeConverter
    fun fromTime(time: Time): Long {
        return time.time
    }

    @TypeConverter
    fun toTime(time: Long): Time {
        return Time(time)
    }
}