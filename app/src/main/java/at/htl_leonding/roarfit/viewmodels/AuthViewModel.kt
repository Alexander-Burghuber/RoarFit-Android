package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.data.LoginRequest
import at.htl_leonding.roarfit.data.LoginResponse
import at.htl_leonding.roarfit.data.Resource
import at.htl_leonding.roarfit.network.KeyFitApi
import at.htl_leonding.roarfit.network.KeyFitApiFactory
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var username: String? = null
    var password: String? = null
    var customerNum: Int? = null
    val loginLD = MutableLiveData<Resource<LoginResponse>>()

    private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()

    fun login(username: String, password: String, customerNum: Int) {
        this.username = username
        this.password = password
        this.customerNum = customerNum
        viewModelScope.launch {
            try {
                val response = keyFitApi.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val loginRes = response.body()!!
                    loginLD.value = when (loginRes.code) {
                        0 -> Resource.Success(loginRes)
                        2 -> Resource.Error("Username or password is wrong")
                        else -> Resource.Error("Received unknown code from the server")
                    }
                } else {
                    loginLD.value = Resource.Error("Received invalid body")
                }
            } catch (e: Exception) {
                val msg = "No connection could be established"
                Log.e("AuthViewModel", msg, e)
                loginLD.value = Resource.Error(msg)
            }
        }
    }
}