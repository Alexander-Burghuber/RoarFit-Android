package at.htl_leonding.roarfit.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.data.Customer
import at.htl_leonding.roarfit.data.LoginResponse
import at.htl_leonding.roarfit.data.Repository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = Repository()
    val loginResult = MutableLiveData<Result<LoginResponse>>()
    val getCustomerResult = MutableLiveData<Result<Customer>>()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(username, password)
                val loginRes = response.body()
                if (response.isSuccessful && loginRes != null) {
                    when (loginRes.code) {
                        0 -> loginResult.value = Result.success(loginRes)
                        2 -> loginResult.value = Result.failure(Exception("Username or password is wrong"))
                        else -> loginResult.value = Result.failure(Exception("Received unknown code from the server"))
                    }
                } else {
                    loginResult.value = Result.failure(Exception("Received invalid body"))
                }
            } catch (e: Exception) {
                val msg = "No connection could be established"
                Log.e("AuthViewModel", msg, e)
                loginResult.value = Result.failure(Exception(msg))
            }
        }
    }

    fun getCustomer(customerNum: String, authToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCustomer(customerNum, authToken)
                val customer = response.body()
                if (response.isSuccessful && customer != null) {
                    getCustomerResult.value = Result.success(customer)
                } else {
                    when (response.code()) {
                        401 -> getCustomerResult.value = Result.failure(Exception("Invalid Authorization Token"))
                        404 -> getCustomerResult.value = Result.failure(Exception("Customer does not exist"))
                        else -> getCustomerResult.value = Result.failure(Exception("An unknown error occurred"))
                    }
                }
            } catch (e: Exception) {
                val msg = "An unknown error occurred"
                Log.e("AuthViewModel", msg, e)
                loginResult.value = Result.failure(Exception(msg))
            }
        }
    }

}
