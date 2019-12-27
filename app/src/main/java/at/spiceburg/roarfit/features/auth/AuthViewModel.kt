package at.spiceburg.roarfit.features.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.LoginResponse
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

class AuthViewModel(private val keyFitApi: KeyFitApi) : ViewModel() {

    val login = MutableLiveData<Resource<LoginResponse>>()
    private val disposables = CompositeDisposable()

    fun login(username: String, password: String, customerNum: Int) {
        login.value = Resource.Loading()
        val login = keyFitApi.login(LoginRequest(username, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { loginRes ->
                    login.value = when (loginRes.code) {
                        0 -> {
                            loginRes.apply {
                                this.username = username
                                this.password = password
                                this.customerNum = customerNum
                            }
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
        disposables.add(login)
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
