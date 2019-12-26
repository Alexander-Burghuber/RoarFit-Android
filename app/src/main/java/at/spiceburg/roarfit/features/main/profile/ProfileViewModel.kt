package at.spiceburg.roarfit.features.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.data.repositories.UserRepository

class ProfileViewModel(private val userRepo: UserRepository) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        userRepo.clear()
    }

    fun getUser(userId: Int, jwt: String): LiveData<Resource<User>> {
        return userRepo.getUser(userId, jwt)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val userRepo: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProfileViewModel(userRepo) as T
        }
    }
}
