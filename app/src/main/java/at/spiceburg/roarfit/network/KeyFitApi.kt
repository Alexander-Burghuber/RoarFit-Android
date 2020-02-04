package at.spiceburg.roarfit.network

import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.db.entities.User
import io.reactivex.Single
import retrofit2.http.*

interface KeyFitApi {

    @POST("login")
    fun login(@Body request: LoginRequest): Single<LoginData>

    @GET("customers/customer/{customerNum}")
    fun getUser(
        @Path("customerNum") customerNum: Int,
        @Header("Authorization") authToken: String
    ): Single<User>
}
