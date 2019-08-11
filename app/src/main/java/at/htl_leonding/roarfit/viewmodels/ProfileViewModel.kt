package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.data.User
import at.htl_leonding.roarfit.repositories.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = UserRepository()
    val userStatus = MutableLiveData<Result<User>>()

    fun getUser(customerNum: Int, authToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUser(customerNum, authToken)
                if (response.isSuccessful) {
                    userStatus.value = Result.success(response.body()!!)
                } else {
                    when (response.code()) {
                        401 -> userStatus.value = Result.failure(Exception("Invalid Authorization Token"))
                        404 -> userStatus.value = Result.failure(Exception("User does not exist"))
                        else -> userStatus.value = Result.failure(Exception("An unexpected error occurred"))
                    }
                }
            } catch (e: Exception) {
                val msg = "An unknown error occurred"
                Log.e("SharedViewModel", msg, e)
                userStatus.value = Result.failure(Exception(msg))
            }
        }
    }

}