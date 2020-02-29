package at.spiceburg.roarfit.data.repositories

import at.spiceburg.roarfit.data.NetworkError
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.dto.EquipmentDTO
import at.spiceburg.roarfit.data.dto.PersonalExerciseDTO
import at.spiceburg.roarfit.data.dto.WorkoutExerciseDTO
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.Single
import retrofit2.HttpException

/**
 * For Workout & Exercise related operations
 */
class WorkoutRepository(private val keyFitApi: KeyFitApi) : DefaultRepository() {

    fun getWorkoutPlan(jwt: String): Single<Result<Array<WorkoutPlan>>> {
        return keyFitApi.getWorkoutPlans(getJwtString(jwt))
            .toResult()
            .onErrorResumeNext { Single.just(handleDefaultNetworkErrors(it)) }
    }

    fun getEquipment(jwt: String): Single<Result<Array<String>>> {
        return keyFitApi.getEquipment(getJwtString(jwt))
            .toResult()
            .onErrorResumeNext { Single.just(handleDefaultNetworkErrors(it)) }
    }

    fun getExerciseTemplates(
        jwt: String,
        equipment: String
    ): Single<Result<Array<ExerciseTemplate>>> {
        return keyFitApi.getExerciseTemplates(getJwtString(jwt), EquipmentDTO(equipment))
            .toResult()
            .onErrorResumeNext { Single.just(handleDefaultNetworkErrors(it)) }
    }

    fun getExerciseHistory(jwt: String, count: Int): Single<Result<Array<Exercise>>> {
        return keyFitApi.getExerciseHistory(getJwtString(jwt), count)
            .toResult()
            .onErrorResumeNext { Single.just(handleDefaultNetworkErrors(it)) }
    }

    fun addPersonalExercise(
        jwt: String,
        personalExerciseDTO: PersonalExerciseDTO
    ): Single<Result<Unit>> {
        return keyFitApi.addPersonalExercise(getJwtString(jwt), personalExerciseDTO)
            .toResult()
            .onErrorResumeNext { Single.just(handleDefaultNetworkErrors(it)) }
    }

    fun addWorkoutExercise(
        jwt: String,
        workoutExerciseDTO: WorkoutExerciseDTO
    ): Single<Result<Unit>> {
        return keyFitApi.addWorkoutExercise(getJwtString(jwt), workoutExerciseDTO)
            .toResult()
            .onErrorResumeNext { e ->
                val res: Result<Unit> = if (e is HttpException && e.code() == 409) {
                    Result.failure(NetworkError.EXERCISE_ALREADY_COMPLETED)
                } else {
                    handleDefaultNetworkErrors(e)
                }
                Single.just(res)
            }
    }

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
    }
}
