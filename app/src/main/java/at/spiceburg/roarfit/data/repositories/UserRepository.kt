package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.ErrorType
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.db.Dao
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UserRepository(private val keyFitApi: KeyFitApi, private val dao: Dao) {

    private val disposables = CompositeDisposable()

    fun getUser(userId: Int): LiveData<UserDB> = dao.getUser(userId)

    fun login(username: String, password: String): LiveData<Response<LoginData>> {
        val liveData = MutableLiveData<Response<LoginData>>()
        val login = keyFitApi.login(LoginRequest(username, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { data ->
                    data.username = username
                    data.password = password
                    liveData.value = Response.Success(data)
                },
                onError = { e ->
                    Log.e(TAG, "Error logging in", e)
                    liveData.value = if (e is UnknownHostException) {
                        Response.Error(ErrorType.SERVER_UNREACHABLE)
                    } else if (e is SocketTimeoutException) {
                        Response.Error(ErrorType.TIMEOUT)
                    } else if (e is HttpException && e.code() == 401) {
                        Response.Error(ErrorType.USERNAME_PASSWORD_WRONG)
                    } else {
                        Response.Error(ErrorType.UNEXPECTED)
                    }
                }
            )
        disposables.add(login)
        return liveData
    }

    fun loadUser(jwt: String): LiveData<Response<Int>> {
        val liveData = MutableLiveData<Response<Int>>()
        val loadUser = keyFitApi.getUser("Bearer $jwt")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { user ->
                    insertUser(user)
                    liveData.value = Response.Success(user.id)
                },
                onError = { e ->
                    Log.e(TAG, "Error loading user", e)
                    liveData.value = when (e) {
                        is HttpException -> when (e.code()) {
                            401 -> Response.Error(ErrorType.JWT_EXPIRED)
                            else -> Response.Error(ErrorType.UNEXPECTED)
                        }
                        is UnknownHostException -> Response.Error(ErrorType.SERVER_UNREACHABLE)
                        is SocketTimeoutException -> Response.Error(ErrorType.TIMEOUT)
                        else -> Response.Error(ErrorType.UNEXPECTED)
                    }
                }
            )
        disposables.add(loadUser)
        return liveData
    }

    private fun insertUser(user: UserDB) {
        val insert = dao.insertUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { e -> Log.e(TAG, "Error inserting user", e) }
            )
        disposables.add(insert)
    }

    fun clear() {
        disposables.clear()
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}
