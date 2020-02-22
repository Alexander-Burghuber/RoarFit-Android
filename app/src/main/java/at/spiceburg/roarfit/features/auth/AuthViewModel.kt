package at.spiceburg.roarfit.features.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.repositories.UserRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class AuthViewModel(private val userRepo: UserRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun login(username: String, password: String): LiveData<Result<LoginData>> {
        val liveData = MutableLiveData<Result<LoginData>>(Result.loading())
        val login = userRepo.login(username, password).subscribeBy(
            onSuccess = { liveData.value = it },
            onError = { Log.e(TAG, "login network call error", it) }
        )
        disposables.add(login)
        return liveData
    }

    fun loadUser(jwt: String): LiveData<Result<UserDB>> {
        val liveData = MutableLiveData<Result<UserDB>>(Result.loading())
        val loadUser = userRepo.loadUser(jwt).subscribeBy(
            onSuccess = { res ->
                if (res.isSuccess()) {
                    val insertUser = userRepo.insertUser(res.data!!)
                        .subscribeBy(
                            onComplete = { liveData.value = res },
                            onError = { Log.e(TAG, "Error inserting user", it) })
                    disposables.add(insertUser)
                } else {
                    liveData.value = res
                }
            },
            onError = { Log.e(TAG, "loadUser network call error", it) }
        )
        disposables.add(loadUser)
        return liveData
    }

    override fun onCleared() {
        disposables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val userRepo: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(userRepo) as T
        }
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
