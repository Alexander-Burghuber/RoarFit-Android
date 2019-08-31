package at.htl_leonding.roarfit.model

import java.util.*

enum class Equipment {
    TREADMILL,
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