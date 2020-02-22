package at.spiceburg.roarfit.data

import java.util.*

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

data class Result<T>(
    val data: T?,
    val error: NetworkErrorType?,
    private var isLoading: Boolean,
    private var lastTimeLoaded: Date? = null
) {

    fun isSuccess(): Boolean {
        return data != null
    }

    fun isLoading(): Boolean {
        return isLoading
    }

    fun shouldReload(): Boolean {
        return Date().time - (lastTimeLoaded?.time ?: 0) >= 30 * 1000 // reload every 30 seconds
    }

    companion object {
        fun <T> success(data: T): Result<T> {
            return Result(data, null, false, Date())
        }

        fun <T> loading(): Result<T> {
            return Result(null, null, true)
        }

        fun <T> failure(error: NetworkErrorType): Result<T> {
            return Result(null, error, false)
        }
    }

    enum class NetworkErrorType {
        SERVER_UNREACHABLE,
        JWT_EXPIRED,
        TIMEOUT,
        UNEXPECTED
    }
}
