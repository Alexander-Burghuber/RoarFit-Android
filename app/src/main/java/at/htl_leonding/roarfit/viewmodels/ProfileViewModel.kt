package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.model.User
import at.htl_leonding.roarfit.network.KeyFitApi
import at.htl_leonding.roarfit.network.KeyFitApiFactory
import kotlinx.coroutines.launch

class ProfileViewModel(private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()) : ViewModel() {
    val userStatus = MutableLiveData<Result<User>>()

    fun getUser(jwt: String, customerNum: Int) {
        viewModelScope.launch {
            try {
                val response = keyFitApi.getUser(customerNum, "Bearer $jwt")
                if (response.isSuccessful) {
                    userStatus.value = Result.success(response.body()!!)
                } else {
                    val msg = when (response.code()) {
                        401 -> "The authorization token has expired."
                        404 -> "The entered customer number is not associated with an user."
                        else -> "An unexpected error occurred."
                    }
                    userStatus.value = Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val msg = "An unknown error occurred"
                Log.e("SharedViewModel", msg, e)
                userStatus.value = Result.failure(Exception(msg))
            }
        }
    }

}