package at.htl_leonding.roarfit.network

import at.htl_leonding.roarfit.model.LoginRequest
import at.htl_leonding.roarfit.model.LoginResponse
import at.htl_leonding.roarfit.model.User
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