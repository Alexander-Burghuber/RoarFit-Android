package at.spiceburg.roarfit.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.LoginResponse
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.network.KeyFitApi
import at.spiceburg.roarfit.network.KeyFitApiFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class AuthViewModel : ViewModel() {
    val loginLD = MutableLiveData<Resource<LoginResponse>>()
    private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()
    private val disposables = CompositeDisposable()

    fun login(username: String, password: String, customerNum: Int) {
        disposables.add(keyFitApi.login(LoginRequest(username, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loginLD.value = Resource.Loading() }
            .subscribeBy(
                onSuccess = { loginRes ->
                    loginLD.value = when (loginRes.code) {
                        0 -> {
                            loginRes.apply {
                                this.username = username
                                this.password = password
                                this.customerNum = customerNum
                            }
                            Resource.Success(loginRes)
                        }
                        2 -> Resource.Error("Username or password is wrong.")
                        else -> Resource.Error("An unknown error occurred.")
                    }
                },
                onError = { e ->
                    loginLD.value = Resource.Error("Server not reachable.")
                    Log.e(TAG, e.message, e)
                }
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
