package at.htl_leonding.roarfit.data.repositories

import at.htl_leonding.roarfit.data.Resource
import at.htl_leonding.roarfit.data.db.UserDao
import at.htl_leonding.roarfit.data.entities.User
import at.htl_leonding.roarfit.network.KeyFitApi
import retrofit2.Response

class UserRepository(private val keyFitApi: KeyFitApi, private val userDao: UserDao) {

    suspend fun getUser(userId: Int, jwt: String): Resource<User> {
        val user = userDao.getUser(userId)
        return if (user != null) {
            Resource.Success(user)
        } else {
            refreshUser(userId, jwt)
        }
    }

    private suspend fun refreshUser(userId: Int, jwt: String): Resource<User> {
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
            val msg = "An unknown error occurred"
            return Resource.Error(msg)
        }
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}