package at.spiceburg.roarfit.features.auth

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.LoginResponse
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.disposables.CompositeDisposable

class AuthViewModel(private val keyFitApi: KeyFitApi) : ViewModel() {

    val login = MutableLiveData<Resource<LoginResponse>>()
    private val disposables = CompositeDisposable()

    // todo: directly return liveData?
    fun login(username: String, password: String, customerNum: Int) {
        Handler().postDelayed({
            val loginRes = LoginResponse(0, "thisisajwt", username, password, customerNum)
            login.value = Resource.Success(loginRes)
        }, 2000)
        // todo uncomment, above statements are temporary
        /*val login = keyFitApi.login(LoginRequest(username, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { loginRes ->
                    login.value = when (loginRes.code) {
                        0 -> {
                            loginRes.username = username
                            loginRes.password = password
                            loginRes.customerNum = customerNum
                            Resource.Success(loginRes)
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

    override fun onCleared() {
        disposables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val keyFitApi: KeyFitApi) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(keyFitApi) as T
        }
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
