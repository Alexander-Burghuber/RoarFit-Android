package at.htl_leonding.roarfit.services

import at.htl_leonding.roarfit.data.Customer
import at.htl_leonding.roarfit.data.LoginRequest
import at.htl_leonding.roarfit.data.LoginResponse
import retrofit2.Response
import retrofit2.http.*

interface WebService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("customers/customer/{customerNum}")
    suspend fun getCustomer(
        @Path("customerNum") customerNum: String,
        @Header("Authorization") authToken: String
    ): Response<Customer>

}