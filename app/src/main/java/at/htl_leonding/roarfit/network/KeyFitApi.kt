package at.htl_leonding.roarfit.network

import at.htl_leonding.roarfit.data.LoginRequest
import at.htl_leonding.roarfit.data.LoginResponse
import at.htl_leonding.roarfit.data.entities.User
import retrofit2.Response
import retrofit2.http.*

interface KeyFitApi {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("customers/customer/{customerNum}")
    suspend fun getUser(
        @Path("customerNum") customerNum: Int,
        @Header("Authorization") authToken: String
    ): Response<User>

}