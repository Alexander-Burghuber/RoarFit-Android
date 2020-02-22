package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.ErrorType
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.dto.EquipmentDTO
import at.spiceburg.roarfit.data.dto.PersonalExerciseDTO
import at.spiceburg.roarfit.data.dto.WorkoutExerciseDTO
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * For Workout & Exercise related operations
 */
class WorkoutRepository(private val keyFitApi: KeyFitApi) {

    private val disposables = CompositeDisposable()

    fun getWorkoutPlan(jwt: String): Single<Result<Array<WorkoutPlan>>> {
        return keyFitApi.getWorkoutPlans(getJwtString(jwt))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toResult()
    }

    fun getEquipment(jwt: String): LiveData<Response<Array<String>>> {
        val liveData = MutableLiveData<Response<Array<String>>>(Response.Loading())
        val loadEquipment = keyFitApi.getEquipment("Bearer $jwt")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { equipment ->
                    liveData.value = Response.Success(equipment)
                },
                onError = { e ->
                    Log.e(TAG, "Error getting equipment", e)
                    liveData.value = handleError(e)
                }
            )
        disposables.add(loadEquipment)
        return liveData
    }

    fun getExerciseTemplates(
        jwt: String,
        equipment: String
    ): LiveData<Response<Array<ExerciseTemplate>>> {
        val liveData = MutableLiveData<Response<Array<ExerciseTemplate>>>(Response.Loading())
        val loadEquipment = keyFitApi.getExerciseTemplates(
            "Bearer $jwt",
            EquipmentDTO(equipment)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { templates ->
                    liveData.value = Response.Success(templates)
                },
                onError = { e ->
                    Log.e(TAG, "Error getting exercise templates", e)
                    liveData.value = handleError(e)
                }
            )
        disposables.add(loadEquipment)
        return liveData
    }

    fun addPersonalExercise(
        jwt: String, templateId: Int, time: String, sets: Int, reps: Int, weight: String?
    ): LiveData<Response<Unit>> {
        val liveData = MutableLiveData<Response<Unit>>(Response.Loading())
        val personalExerciseDTO = PersonalExerciseDTO(templateId, time, sets, reps, weight)
        val addPersonalExercise = keyFitApi.addPersonalExercise("Bearer $jwt", personalExerciseDTO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    liveData.value = Response.Success(Unit)
                },
                onError = { e ->
                    Log.e(TAG, "Error adding personal exercise", e)
                    liveData.value = handleError(e)
                }
            )
        disposables.add(addPersonalExercise)
        return liveData
    }

    fun addWorkoutExercise(
        jwt: String, exerciseId: Int, time: String, sets: Int, reps: Int, weight: String?
    ): LiveData<Response<Unit>> {
        val liveData = MutableLiveData<Response<Unit>>(Response.Loading())
        val workoutExerciseDTO = WorkoutExerciseDTO(exerciseId, time, sets, reps, weight)
        val addWorkoutExercise = keyFitApi.addWorkoutExercise("Bearer $jwt", workoutExerciseDTO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    liveData.value = Response.Success(Unit)
                },
                onError = { e ->
                    Log.e(TAG, "Error adding personal exercise", e)
                    liveData.value = handleError(e)
                }
            )
        disposables.add(addWorkoutExercise)
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    private fun <T> handleError(e: Throwable): Response.Error<T> {
        when (e) {
            is UnknownHostException -> {
                return Response.Error(ErrorType.SERVER_UNREACHABLE)
            }
            is HttpException -> {
                when {
                    e.code() == 400 -> {
                        return Response.Error(ErrorType.INVALID_INPUT)
                    }
                    e.code() == 401 -> {
                        return Response.Error(ErrorType.JWT_EXPIRED)
                    }
                    e.code() == 409 -> {
                        return Response.Error(ErrorType.EXERCISE_ALREADY_COMPLETED)
                    }
                }
            }
            is SocketTimeoutException -> {
                return Response.Error(ErrorType.TIMEOUT)
            }
        }
        return Response.Error(ErrorType.UNEXPECTED)
    }

    private fun <T> Single<T>.toResult(): Single<Result<T>> {
        return map { Result.success(it) }
            .onErrorResumeNext { Single.just(handleNetworkError(it)) }
    }

    private fun <T> handleNetworkError(e: Throwable): Result<T> {
        return if (e is UnknownHostException) {
            Result.failure(Result.NetworkErrorType.SERVER_UNREACHABLE)
        } else if (e is HttpException && e.code() == 401) {
            Result.failure(Result.NetworkErrorType.JWT_EXPIRED)
        } else if (e is SocketTimeoutException) {
            Result.failure(Result.NetworkErrorType.TIMEOUT)
        } else {
            Log.e(TAG, "An unknown network error occurred", e)
            Result.failure(Result.NetworkErrorType.UNEXPECTED)
        }
    }

    private fun getJwtString(jwt: String): String {
        return "Bearer $jwt"
    }

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
    }
}