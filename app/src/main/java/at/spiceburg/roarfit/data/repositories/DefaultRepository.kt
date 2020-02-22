package at.spiceburg.roarfit.data.repositories

import android.util.Log
import at.spiceburg.roarfit.data.NetworkError
import at.spiceburg.roarfit.data.Result
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class DefaultRepository {

    fun <T> Single<T>.toResult(): Single<Result<T>> {
        return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { Result.success(it) }
    }

    fun <T> handleDefaultNetworkErrors(e: Throwable): Result<T> {
        val error: NetworkError = if (e is UnknownHostException) {
            NetworkError.SERVER_UNREACHABLE
        } else if (e is HttpException && e.code() == 401) {
            NetworkError.JWT_EXPIRED
        } else if (e is SocketTimeoutException) {
            NetworkError.TIMEOUT
        } else {
            Log.e(TAG, "An unknown network error occurred", e)
            NetworkError.UNEXPECTED
        }
        return Result.failure(error)
    }

    fun getJwtString(jwt: String): String {
        return "Bearer $jwt"
    }

    companion object {
        private val TAG = DefaultRepository::class.java.simpleName
    }
}
