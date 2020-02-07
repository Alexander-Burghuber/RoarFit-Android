package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException

class WorkoutRepository(private val keyFitApi: KeyFitApi) {

    private val disposables = CompositeDisposable()

    fun getWorkoutPlan(jwt: String): LiveData<Response<WorkoutPlan>> {
        val liveData = MutableLiveData<Response<WorkoutPlan>>(Response.Loading())
        val loadWorkoutPlans = keyFitApi.getWorkoutPlans("Bearer $jwt")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { workoutPlans ->
                    liveData.value = Response.Success(workoutPlans[0])
                },
                onError = { e ->
                    Log.e(TAG, "Error getting workoutplans", e)
                    liveData.value = if (e is UnknownHostException) {
                        Response.Error("Server not reachable")
                    } else if (e is HttpException && e.code() == 401) {
                        // jwt is invalid
                        Response.Error("Please re-login", true)
                    } else {
                        Response.Error("An unknown error occurred")
                    }
                }
            )
        disposables.add(loadWorkoutPlans)
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
    }
}