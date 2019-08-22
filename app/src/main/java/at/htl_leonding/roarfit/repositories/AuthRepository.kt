package at.htl_leonding.roarfit.repositories

import at.htl_leonding.roarfit.model.LoginRequest
import at.htl_leonding.roarfit.model.LoginResponse
import at.htl_leonding.roarfit.network.KeyFitApi
import at.htl_leonding.roarfit.network.KeyFitApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AuthRepository {
    private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return withContext(Dispatchers.IO) {
            keyFitApi.login(LoginRequest(username, password))
        }
    }

}
