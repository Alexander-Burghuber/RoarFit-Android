package at.spiceburg.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.LoginResponse
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.network.KeyFitApi
import at.spiceburg.roarfit.network.KeyFitApiFactory
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    val loginLD = MutableLiveData<Resource<LoginResponse>>()
    private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()

    fun login(username: String, password: String, customerNum: Int) {
        viewModelScope.launch {
            try {
                val response = keyFitApi.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val loginRes = response.body()!!
                    loginRes.username = username
                    loginRes.password = password
                    loginRes.customerNum = customerNum
                    loginLD.value = when (loginRes.code) {
                        0 -> Resource.Success(loginRes)
                        2 -> Resource.Error("Username or password is wrong.")
                        else -> Resource.Error("An unknown error occurred.")
                    }
                } else {
                    loginLD.value = Resource.Error("An unknown error occurred.")
                }
            } catch (e: Exception) {
                val msg = "Server not reachable. Please ensure you are connected to the internet."
                Log.e("AuthViewModel", msg, e)
                loginLD.value = Resource.Error(msg)
            }
        }
    }
}
