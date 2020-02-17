package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.EquipmentDTO
import at.spiceburg.roarfit.data.ErrorType
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.network.KeyFitApi
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

    fun getWorkoutPlan(jwt: String): LiveData<Response<Array<WorkoutPlan>>> {
        val liveData = MutableLiveData<Response<Array<WorkoutPlan>>>(Response.Loading())
        val loadWorkoutPlans = keyFitApi.getWorkoutPlans("Bearer $jwt")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { workoutPlans ->
                    liveData.value = Response.Success(workoutPlans)
                },
                onError = { e ->
                    Log.e(TAG, "Error getting workoutplans", e)
                    liveData.value = handleError(e)
                }
            )
        disposables.add(loadWorkoutPlans)
        return liveData
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
        val loadEquipment = keyFitApi.getExerciseTemplates("Bearer $jwt", EquipmentDTO(equipment))
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

    fun clear() {
        disposables.clear()
    }

    private fun <T> handleError(e: Throwable): Response.Error<T> {
        return if (e is UnknownHostException) {
            Response.Error(ErrorType.SERVER_UNREACHABLE)
        } else if (e is HttpException && e.code() == 401) {
            Response.Error(ErrorType.JWT_EXPIRED)
        } else if (e is SocketTimeoutException) {
            Response.Error(ErrorType.TIMEOUT)
        } else {
            Response.Error(ErrorType.UNEXPECTED)
        }
    }

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
    }
}