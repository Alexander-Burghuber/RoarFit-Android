package at.htl_leonding.roarfit.repositories

import at.htl_leonding.roarfit.data.User
import at.htl_leonding.roarfit.services.WebService
import at.htl_leonding.roarfit.services.WebServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository {
    private val webService: WebService = WebServiceFactory.create()

    suspend fun getUser(customerNum: Int, authToken: String): Response<User> {
        return withContext(Dispatchers.IO) {
            webService.getUser(customerNum, "Bearer $authToken")
        }
    }

}