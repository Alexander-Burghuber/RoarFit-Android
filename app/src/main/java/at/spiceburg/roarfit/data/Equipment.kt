package at.spiceburg.roarfit.data

import com.google.gson.annotations.SerializedName

enum class Equipment(val string: String) {
    @SerializedName("Laufband")
    TREADMILL("Laufband"), // ah, yes. enslaved road
    @SerializedName("Crosstrainer")
    CROSS_TRAINER("Crosstrainer"),
    @SerializedName("Fahrradergometer")
    EXERCYCLE("Fahrradergometer"),
    @SerializedName("Beinstrecker")
    LEG_EXTENSION("Beinstrecker"),
    @SerializedName("Kurzhanteln")
    DUMBBELL("Kurzhanteln"),
    @SerializedName("T-Hantel")
    BARBELL("T-Hantel"),
    @SerializedName("Hex-Bar")
    HEX_BAR("Hex-Bar");

    /**
     * Converts the name of the enum to a readable string for output purposes.
     */
    /*override fun toString(): String {
        val words = name.toLowerCase(Locale.ENGLISH).split("_")

        var string = ""
        for (word in words) {
            string += word.capitalize() + " "
        }

        return string.trim()
    }*/
}
