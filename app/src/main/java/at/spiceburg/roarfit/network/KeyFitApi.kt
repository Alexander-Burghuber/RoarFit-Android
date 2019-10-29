package at.spiceburg.roarfit.network

import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.LoginResponse
import at.spiceburg.roarfit.data.entities.User
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface KeyFitApi {

    @POST("login")
    fun login(@Body request: LoginRequest): Single<LoginResponse>

    @GET("customers/customer/{customerNum}")
    suspend fun getUser(
        @Path("customerNum") customerNum: Int,
        @Header("Authorization") authToken: String
    ): Response<User>
}
