package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.model.LoginRequest
import at.htl_leonding.roarfit.model.LoginResponse
import at.htl_leonding.roarfit.network.KeyFitApi
import at.htl_leonding.roarfit.network.KeyFitApiFactory
import kotlinx.coroutines.launch

class AuthViewModel(private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()) : ViewModel() {
    var username: String? = null
    var password: String? = null
    var customerNum: Int? = null

    val loginLiveData = MutableLiveData<Result<LoginResponse>>()

    fun login(username: String, password: String, customerNum: Int) {
        this.username = username
        this.password = password
        this.customerNum = customerNum
        viewModelScope.launch {
            try {
                val response = keyFitApi.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val loginRes = response.body()!!
                    when (loginRes.code) {
                        0 -> loginLiveData.value = Result.success(loginRes)
                        2 -> loginLiveData.value = Result.failure(Exception("Username or password is wrong"))
                        else -> loginLiveData.value =
                            Result.failure(Exception("Received unknown code from the server"))
                    }
                } else {
                    loginLiveData.value = Result.failure(Exception("Received invalid body"))
                }
            } catch (e: Exception) {
                val msg = "No connection could be established"
                Log.e("AuthViewModel", msg, e)
                loginLiveData.value = Result.failure(Exception(msg))
            }
        }
    }

}
