package at.spiceburg.roarfit.data

data class LoginData(
    val code: Int,
    val token: String,
    var username: String,
    var password: String,
    var customerNum: Int
)
