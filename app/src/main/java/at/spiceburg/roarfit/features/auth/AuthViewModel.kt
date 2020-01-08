package at.spiceburg.roarfit.features.auth

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.disposables.CompositeDisposable

class AuthViewModel(
    private val keyFitApi: KeyFitApi,
    private val userRepo: UserRepository
) : ViewModel() {

    val login = MutableLiveData<Resource<LoginData>>()
    private val disposables = CompositeDisposable()

    fun login(username: String, password: String, customerNum: Int) {
        Handler().postDelayed({
            val loginRes = LoginData(0, "thisisajwt", username, password, customerNum)
            login.value = Resource.Success(loginRes)
        }, 500)
        /*val login = keyFitApi.login(LoginRequest(username, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { data ->
                    login.value = when (data.code) {
                        0 -> {
                            data.username = username
                            data.password = password
                            data.customerNum = customerNum
                            Resource.Success(data)
                        }
                        2 -> Resource.Error("Username or password is wrong")
                        else -> Resource.Error("An unknown error occurred")
                    }
                },
                onError = { e ->
                    Log.e(TAG, e.message, e)
                    val msg = if (e is UnknownHostException) {
                        "Server not reachable"
                    } else {
                        "An unknown error occurred"
                    }
                    login.value = Resource.Error(msg)
                }
            )
        disposables.add(login)*/
    }

    fun loadUser(userId: Int, jwt: String): LiveData<Status> {
        return userRepo.loadUser(userId, jwt)
    }

    override fun onCleared() {
        disposables.clear()
        userRepo.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val keyFitApi: KeyFitApi,
        private val userRepo: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(keyFitApi, userRepo) as T
        }
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
