package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Resource
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

    fun getUser(userId: Int, jwt: String): LiveData<Resource<User>> {
        val liveData = MutableLiveData<Resource<User>>(Resource.Loading())
        disposables.addAll(
            userDao.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { user -> liveData.value = Resource.Loading(user) },
            keyFitApi.getUser(userId, "Bearer $jwt")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { user -> liveData.value = Resource.Success(user) },
                    onError = { e ->
                        liveData.value = when (e) {
                            is HttpException -> {
                                when (e.code()) {
                                    401 -> Resource.Error(
                                        "The authorization token has expired.",
                                        true
                                    )
                                    404 -> Resource.Error(
                                        "The entered customer number is not associated with an user.",
                                        true
                                    )
                                    else -> {
                                        Log.e(TAG, e.message(), e)
                                        Resource.Error("An unexpected error occurred.", true)
                                    }
                                }
                            }
                            is UnknownHostException -> Resource.Error("Server not reachable.")
                            else -> {
                                Log.e(TAG, e.message, e)
                                Resource.Error("An unexpected error occurred.")
                            }
                        }
                    }
                )
        )
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}
