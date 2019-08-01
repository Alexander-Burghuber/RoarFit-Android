package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.data.LoginResponse
import at.htl_leonding.roarfit.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    val loginResStatus = MutableLiveData<Result<LoginResponse>>()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                if (response.isSuccessful) {
                    val loginRes = response.body()!!
                    when (loginRes.code) {
                        0 -> loginResStatus.value = Result.success(loginRes)
                        2 -> loginResStatus.value = Result.failure(Exception("Username or password is wrong"))
                        else -> loginResStatus.value =
                            Result.failure(Exception("Received unknown code from the server"))
                    }
                } else {
                    loginResStatus.value = Result.failure(Exception("Received invalid body"))
                }
            } catch (e: Exception) {
                val msg = "No connection could be established"
                Log.e("AuthViewModel", msg, e)
                loginResStatus.value = Result.failure(Exception(msg))
            }
        }
    }

}
