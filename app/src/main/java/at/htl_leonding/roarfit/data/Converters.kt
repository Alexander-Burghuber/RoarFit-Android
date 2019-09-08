package at.htl_leonding.roarfit.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun stringToPeriod(string: String): Period {
        return Period(string)
    }

    @TypeConverter
    fun periodToString(period: Period): String {
        return period.toString()
    }
}