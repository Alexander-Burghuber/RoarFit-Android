package at.spiceburg.roarfit.data

sealed class Status(
    var message: String? = null,
    var logout: Boolean = false
) {
    class Success : Status()
    class Loading : Status()
    class Error : Status()
}
