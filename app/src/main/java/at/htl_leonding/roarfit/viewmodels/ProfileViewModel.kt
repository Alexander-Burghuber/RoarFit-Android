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

    fun getUser(jwt: String, customerNum: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getUser(jwt, customerNum)
                if (response.isSuccessful) {
                    userStatus.value = Result.success(response.body()!!)
                } else {
                    userStatus.value = Result.failure(Exception(response.code().toString()))
                }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "An unknown error occurred", e)
                userStatus.value = Result.failure(Exception())
            }
        }
    }

}