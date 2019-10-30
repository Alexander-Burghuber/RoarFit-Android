package at.spiceburg.roarfit.data

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val logout: Boolean? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, logout: Boolean = false, data: T? = null) :
        Resource<T>(data, message, logout)
}
