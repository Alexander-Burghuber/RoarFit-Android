package at.spiceburg.roarfit.network

import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.dto.EquipmentDTO
import at.spiceburg.roarfit.data.dto.LoginRequest
import at.spiceburg.roarfit.data.dto.PersonalExerciseDTO
import at.spiceburg.roarfit.data.dto.WorkoutExerciseDTO
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.User
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
    fun getUser(@Header("Authorization") token: String): Single<User>

    @GET("workoutplans")
    fun getWorkoutPlans(@Header("Authorization") token: String): Single<Array<WorkoutPlan>>

    @GET("equipment")
    fun getEquipment(@Header("Authorization") token: String): Single<Array<String>>

    @POST("templates")
    fun getExerciseTemplates(@Header("Authorization") token: String, @Body equipmentDTO: EquipmentDTO): Single<Array<ExerciseTemplate>>

    @POST("personal-exercise")
    fun addPersonalExercise(@Header("Authorization") token: String, @Body personalExerciseDTO: PersonalExerciseDTO): Single<Unit>

    @POST("workout-exercise")
    fun addWorkoutExercise(@Header("Authorization") token: String, @Body workoutExerciseDTO: WorkoutExerciseDTO): Single<Unit>

    @GET("exercise-history/{count}")
    fun getExerciseHistory(@Header("Authorization") token: String, @Path("count") count: Int): Single<Array<Exercise>>
}
