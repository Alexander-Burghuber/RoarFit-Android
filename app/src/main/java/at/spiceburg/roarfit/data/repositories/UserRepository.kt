package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.db.UserDao
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException

class UserRepository(private val keyFitApi: KeyFitApi, private val userDao: UserDao) {

    private val disposables = CompositeDisposable()

    fun getUser(userId: Int): LiveData<User?> = userDao.getUser(userId)

    fun loadUser(userId: Int, jwt: String): LiveData<Status> {
        val liveData = MutableLiveData<Status>(Status.Loading())
        val loadUser = keyFitApi.getUser(userId, "Bearer $jwt")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { user ->
                    insertUser(user)
                    liveData.value = Status.Success()
                },
                onError = { e ->
                    val status = Status.Error()
                    if (e is HttpException) {
                        status.logout = true
                        status.message = when (e.code()) {
                            401 -> "The authorization token has expired"
                            404 -> "The entered customer number is not associated with an user."
                            else -> "An unexpected error occurred"
                        }
                    } else if (e is UnknownHostException) {
                        status.message = "Server not reachable"
                    }
                    liveData.value = status
                }
            )
        disposables.add(loadUser)
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    private fun insertUser(user: User) {
        val insert = userDao.insertUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { e ->
                    Log.e(TAG, e.message, e)
                }
            )
        disposables.add(insert)
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}
