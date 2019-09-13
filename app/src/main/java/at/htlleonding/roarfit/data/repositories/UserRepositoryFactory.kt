package at.htlleonding.roarfit.data.repositories

import android.content.Context
import at.htlleonding.roarfit.data.db.AppDatabase
import at.htlleonding.roarfit.network.KeyFitApiFactory

class UserRepositoryFactory {
    companion object {
        private var userRepository: UserRepository? = null
        fun create(context: Context): UserRepository {
            if (userRepository == null) {
                val keyFitApi = KeyFitApiFactory.create()
                val userDao = AppDatabase.getDatabase(context).userDao()
                userRepository = UserRepository(keyFitApi, userDao)
            }
            return userRepository!!
        }
    }
}