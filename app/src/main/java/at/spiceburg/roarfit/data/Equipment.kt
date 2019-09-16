package at.spiceburg.roarfit.data

import java.util.*

enum class Equipment {
    TREADMILL, // ah, yes. enslaved road
    CROSS_TRAINER,
    EXERCYCLE;

    /**
     * Converts the name of the enum to a readable string for output purposes.
     */
    override fun toString(): String {
        val words = name.toLowerCase(Locale.ENGLISH).split("_")

        var string = ""
        for (word in words) {
            string += word.capitalize() + " "
        }

        return string.trim()
    }
}