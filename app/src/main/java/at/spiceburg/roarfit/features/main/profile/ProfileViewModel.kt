package at.spiceburg.roarfit.features.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.repositories.UserRepository

class ProfileViewModel(
    private val userId: Int,
    private val userRepo: UserRepository
) : ViewModel() {

    val user = userRepo.getUser(userId)

    fun loadUser(jwt: String): LiveData<Status> {
        return userRepo.loadUser(userId, jwt)
    }

    override fun onCleared() {
        userRepo.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userId: Int,
        private val userRepo: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProfileViewModel(userId, userRepo) as T
        }
    }
}
