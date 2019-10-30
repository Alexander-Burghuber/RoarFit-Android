package at.spiceburg.roarfit.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.UserRepositoryFactory

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        userRepository.clear()
    }

    fun getUser(userId: Int, jwt: String): LiveData<Resource<User>> {
        return userRepository.getUser(userId, jwt)
    }

    class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProfileViewModel(UserRepositoryFactory.create(context)) as T
        }
    }
}
