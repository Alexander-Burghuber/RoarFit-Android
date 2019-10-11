package at.spiceburg.roarfit.data.db

import androidx.room.TypeConverter
import at.spiceburg.roarfit.data.Period

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
