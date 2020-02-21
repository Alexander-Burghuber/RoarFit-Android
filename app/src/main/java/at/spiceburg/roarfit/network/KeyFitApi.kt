package at.spiceburg.roarfit.network

import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.dto.EquipmentDTO
import at.spiceburg.roarfit.data.dto.LoginRequest
import at.spiceburg.roarfit.data.dto.PersonalExerciseDTO
import at.spiceburg.roarfit.data.dto.WorkoutExerciseDTO
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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

    @GET("equipment")
    fun getEquipment(@Header("Authorization") token: String): Single<Array<String>>

    @POST("templates")
    fun getExerciseTemplates(@Header("Authorization") token: String, @Body equipmentDTO: EquipmentDTO): Single<Array<ExerciseTemplate>>

    @POST("personal-exercise")
    fun addPersonalExercise(@Header("Authorization") token: String, @Body personalExerciseDTO: PersonalExerciseDTO): Completable

    @POST("workout-exercise")
    fun addWorkoutExercise(@Header("Authorization") token: String, @Body workoutExerciseDTO: WorkoutExerciseDTO): Completable
}
