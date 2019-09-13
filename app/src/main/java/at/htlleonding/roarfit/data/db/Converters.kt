package at.htlleonding.roarfit.data.db

import androidx.room.TypeConverter
import at.htlleonding.roarfit.data.Period

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