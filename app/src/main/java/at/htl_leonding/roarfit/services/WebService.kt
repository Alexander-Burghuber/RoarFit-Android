package at.htl_leonding.roarfit.services

import at.htl_leonding.roarfit.data.LoginRequest
import at.htl_leonding.roarfit.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WebService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}