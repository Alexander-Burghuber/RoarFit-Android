package at.spiceburg.roarfit.data.db

import androidx.room.TypeConverter
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.Period

class Converters {

    @TypeConverter
    fun stringToEquipment(string: String?): Equipment? {
        return string?.let {
            Equipment.values().find { eq -> eq.string == it }
        }
    }

    @TypeConverter
    fun equipmentToString(equipment: Equipment?): String? {
        return equipment?.string
    }

    @TypeConverter
    fun stringToPeriod(string: String): Period {
        return Period(string)
    }

    @TypeConverter
    fun periodToString(period: Period): String {
        return period.toString()
    }
}
