package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.htl_leonding.roarfit.data.LoginResponse
import at.htl_leonding.roarfit.data.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val repository = Repository()
    val loginResult = MutableLiveData<LoginResponse>()

    fun login(username: String, password: String) {
        repository.login(username, password, object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() is LoginResponse) {
                    loginResult.value = response.body()
                } else {
                    Log.e("AuthViewModel", "Received unsuccessful body")
                    loginResult.value = null
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("AuthViewModel", "Failure logging in", t)
                loginResult.value = null
            }
        })
    }
}