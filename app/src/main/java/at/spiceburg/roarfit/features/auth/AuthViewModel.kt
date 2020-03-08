package at.spiceburg.roarfit.features.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.repositories.UserRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class AuthViewModel(private val userRepo: UserRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun login(username: String, password: String): LiveData<Result<LoginData>> {
        val liveData = MutableLiveData<Result<LoginData>>(Result.loading())
        val login = userRepo.login(username, password).subscribeBy(
            onSuccess = { res ->
                if (res.isSuccess()) {
                    val loginData: LoginData = res.data!!
                    loginData.username = username
                    loginData.password = password
                }
                liveData.value = res
            },
            onError = { Log.e(TAG, "login network call error", it) }
        )
        disposables.add(login)
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
