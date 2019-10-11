package at.spiceburg.roarfit.data.repositories

import android.util.Log
import at.spiceburg.roarfit.data.Resource
import at.spiceburg.roarfit.data.db.UserDao
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.network.KeyFitApi
import retrofit2.Response

class UserRepository(private val keyFitApi: KeyFitApi, private val userDao: UserDao) {

    suspend fun getUser(userId: Int): User? {
        return userDao.getUser(userId)
    }

    suspend fun refreshUser(userId: Int, jwt: String): Resource<User> {
        try {
            val response: Response<User> = keyFitApi.getUser(userId, "Bearer $jwt")
            return if (response.isSuccessful) {
                val user = response.body()!!
                userDao.insertUser(user)
                Resource.Success(user)
            } else {
                val msg = when (response.code()) {
                    401 -> "The authorization token has expired."
                    404 -> "The entered customer number is not associated with an user."
                    else -> "An unexpected error occurred."
                }
                Resource.Error(msg)
            }
        } catch (e: Exception) {
            val msg = "An unknown error occurred."
            Log.e(TAG, msg, e)
            return Resource.Error(msg)
        }
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}
