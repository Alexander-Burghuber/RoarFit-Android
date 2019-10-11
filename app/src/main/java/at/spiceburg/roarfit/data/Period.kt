package at.spiceburg.roarfit.data

class Period {
    var minutes: Int
    var seconds: Int

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
