package at.htlleonding.roarfit.data

data class LoginResponse(
    val code: Int,
    val token: String,
    var username: String?,
    var password: String?,
    var customerNum: Int?
)
