package at.htl_leonding.roarfit.data

import at.htl_leonding.roarfit.services.WebService
import retrofit2.Call
import retrofit2.Callback
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

    fun login(username: String, password: String, callback: Callback<LoginResponse>) {
        val call: Call<LoginResponse> = webService.login(LoginRequest(username, password))
        call.enqueue(callback)
    }

}