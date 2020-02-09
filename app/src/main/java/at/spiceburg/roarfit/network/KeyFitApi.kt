package at.spiceburg.roarfit.network

import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.LoginRequest
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.entities.UserExercise
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import io.reactivex.Single
import retrofit2.http.*

interface KeyFitApi {

    /*@POST("login")
    fun login(@Body request: LoginRequest): Single<LoginData>

    @GET("customers/customer/{customerNum}")
    fun getUser(
        @Path("customerNum") customerNum: Int,
        @Header("Authorization") authToken: String
    ): Single<User>*/

    @POST("login")
    fun login(@Body request: LoginRequest): Single<LoginData>

    @GET("user")
    fun getUser(@Header("Authorization") token: String): Single<UserDB>

    @GET("workoutplans")
    fun getWorkoutPlans(@Header("Authorization") token: String): Single<Array<WorkoutPlan>>

    @GET("exercises/{workoutId}")
    fun getExercises(@Header("Authorization") token: String, @Path("workoutId") workoutId: Int): Single<List<UserExercise>>
}
