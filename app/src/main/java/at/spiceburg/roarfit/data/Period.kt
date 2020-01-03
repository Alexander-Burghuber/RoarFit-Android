package at.spiceburg.roarfit.data

class Period {

    var minutes: Int
        set(value) {
            if (value !in 0..59) throw IllegalArgumentException("Invalid minutes")
            field = value
        }
    var seconds: Int
        set(value) {
            if (value !in 0..59) throw IllegalArgumentException("Invalid seconds")
            field = value
        }

    constructor(minutes: Int, seconds: Int) {
        this.minutes = minutes
        this.seconds = seconds
    }

    constructor(text: String) {
        val units = text.split(":").map { x -> x.toInt() }
        this.minutes = units[0]
        this.seconds = units[1]
    }

    override fun toString(): String {
        return "$minutes:$seconds"
    }
}
