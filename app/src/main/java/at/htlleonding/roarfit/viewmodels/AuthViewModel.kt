package at.htlleonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htlleonding.roarfit.data.LoginRequest
import at.htlleonding.roarfit.data.LoginResponse
import at.htlleonding.roarfit.data.Resource
import at.htlleonding.roarfit.network.KeyFitApi
import at.htlleonding.roarfit.network.KeyFitApiFactory
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