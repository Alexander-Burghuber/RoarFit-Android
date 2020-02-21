package at.spiceburg.roarfit.data

sealed class Response<T>(
    val data: T? = null,
    val errorType: ErrorType? = null
) {
    class Success<T>(data: T) : Response<T>(data)
    class Loading<T>(data: T? = null) : Response<T>(data)
    class Error<T>(errorType: ErrorType, data: T? = null) :
        Response<T>(data, errorType)
}

enum class ErrorType {
    SERVER_UNREACHABLE,
    USERNAME_PASSWORD_WRONG,
    JWT_EXPIRED,
    INVALID_INPUT,
    EXERCISE_ALREADY_COMPLETED,
    TIMEOUT,
    UNEXPECTED
}
