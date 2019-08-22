package at.htl_leonding.roarfit.repositories

import at.htl_leonding.roarfit.model.User
import at.htl_leonding.roarfit.network.KeyFitApi
import at.htl_leonding.roarfit.network.KeyFitApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository {
    private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()

    suspend fun getUser(jwt: String, customerNum: Int): Response<User> {
        return withContext(Dispatchers.IO) {
            keyFitApi.getUser(customerNum, "Bearer $jwt")
        }
    }

}