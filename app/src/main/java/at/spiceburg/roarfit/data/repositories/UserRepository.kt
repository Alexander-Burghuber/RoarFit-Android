package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.db.dao.UserDao
import at.spiceburg.roarfit.data.db.entities.User
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException

class UserRepository(private val keyFitApi: KeyFitApi, private val userDao: UserDao) {

    private val disposables = CompositeDisposable()

    fun getUser(userId: Int): LiveData<User> = userDao.getUser(userId)

    fun loadUser(jwt: String): LiveData<Resource<Int>> {
        val liveData = MutableLiveData<Resource<Int>>()
        val loadUser = keyFitApi.getUser("Bearer $jwt")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { user ->
                    insertUser(user)
                    liveData.value = Resource.Success(user.id)
                },
                onError = { e ->
                    val msg: String = when (e) {
                        is HttpException -> {
                            when (e.code()) {
                                401 -> "The authorization token has expired"
                                else -> "An unexpected error occurred"
                            }
                        }
                        is UnknownHostException -> {
                            "Server not reachable"
                        }
                        else -> {
                            Log.e(TAG, e.message, e)
                            "An unknown error occurred"
                        }
                    }
                    liveData.value = Resource.Error(msg)
                }
            )
        disposables.add(loadUser)
        return liveData
    }

    private fun insertUser(user: User) {
        val insert = userDao.insertUser(user)
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
