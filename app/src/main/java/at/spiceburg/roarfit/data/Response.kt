package at.spiceburg.roarfit.data

sealed class Response<T>(
    val data: T? = null,
    val message: String? = null,
    val logout: Boolean? = null
) {
    class Success<T>(data: T) : Response<T>(data)
    class Loading<T>(data: T? = null) : Response<T>(data)
    class Error<T>(message: String, logout: Boolean = false, data: T? = null) :
        Response<T>(data, message, logout)
}
