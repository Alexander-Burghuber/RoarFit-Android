package at.spiceburg.roarfit.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.repositories.UserRepository

class AuthViewModel(private val userRepo: UserRepository) : ViewModel() {

    fun login(username: String, password: String): LiveData<Response<LoginData>> {
        return userRepo.login(username, password)
    }

    fun loadUser(jwt: String): LiveData<Response<Int>> {
        return userRepo.loadUser(jwt)
    }

    override fun onCleared() {
        userRepo.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val userRepo: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AuthViewModel(userRepo) as T
        }
    }

    companion object {
        private val TAG = AuthViewModel::class.java.simpleName
    }
}
