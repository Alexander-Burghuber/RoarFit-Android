package at.htl_leonding.roarfit.data.repositories

import android.content.Context
import at.htl_leonding.roarfit.data.db.AppDatabase
import at.htl_leonding.roarfit.network.KeyFitApiFactory

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