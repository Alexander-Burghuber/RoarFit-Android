package at.htl_leonding.roarfit.repositories

import at.htl_leonding.roarfit.data.LoginRequest
import at.htl_leonding.roarfit.data.LoginResponse
import at.htl_leonding.roarfit.services.WebService
import at.htl_leonding.roarfit.services.WebServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository {
    private val webService: WebService = WebServiceFactory.create()

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return withContext(Dispatchers.IO) {
            webService.login(LoginRequest(username, password))
        }
    }

}
