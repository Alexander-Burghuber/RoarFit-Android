package at.htl_leonding.roarfit.repositories

import at.htl_leonding.roarfit.data.User
import at.htl_leonding.roarfit.network.WebService
import at.htl_leonding.roarfit.network.WebServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository {
    private val webService: WebService = WebServiceFactory.create()

    suspend fun getUser(jwt: String, customerNum: Int): Response<User> {
        return withContext(Dispatchers.IO) {
            webService.getUser(customerNum, "Bearer $jwt")
        }
    }

}