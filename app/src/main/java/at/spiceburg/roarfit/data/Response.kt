package at.spiceburg.roarfit.data

import java.util.*

data class Result<T>(
    val data: T?, val error: NetworkError?,
    private var isLoading: Boolean, private var lastTimeLoaded: Date? = null
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

        fun <T> failure(error: NetworkError): Result<T> {
            return Result(null, error, false)
        }
    }
}

enum class NetworkError {
    // defaults
    SERVER_UNREACHABLE,
    JWT_EXPIRED,
    TIMEOUT,
    UNEXPECTED,
    // other
    EXERCISE_ALREADY_COMPLETED,
    USERNAME_PASSWORD_WRONG,
}

/*enum class ErrorType {
    SERVER_UNREACHABLE,
    USERNAME_PASSWORD_WRONG,
    JWT_EXPIRED,
    INVALID_INPUT,
    EXERCISE_ALREADY_COMPLETED,
    TIMEOUT,
    UNEXPECTED
}*/
