package at.htl_leonding.roarfit.data

import at.htl_leonding.roarfit.services.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository {
    private val webService: WebService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://staging.key.fit/lionsoft/app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        webService = retrofit.create(WebService::class.java)
    }

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return withContext(Dispatchers.IO) {
            webService.login(LoginRequest(username, password))
        }
    }

    suspend fun getCustomer(customerNum: String, authToken: String): Response<Customer> {
        return withContext(Dispatchers.IO) {
            webService.getCustomer(customerNum, "Bearer $authToken")
        }
    }

}
