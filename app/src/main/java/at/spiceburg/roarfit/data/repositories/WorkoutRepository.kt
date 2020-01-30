package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Period
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.db.WorkoutDao
import at.spiceburg.roarfit.data.db.WorkoutPlanDao
import at.spiceburg.roarfit.data.entities.Workout
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class WorkoutRepository(
    private val keyFitApi: KeyFitApi,
    private val workoutDao: WorkoutDao,
    private val workoutPlanDao: WorkoutPlanDao
) {

    private val disposables = CompositeDisposable()

    fun getWorkoutPlans(userId: Int): LiveData<Array<WorkoutPlan>?> {
        return workoutPlanDao.getWorkoutPlans(userId)
    }

    fun loadWorkoutPlans(userId: Int, jwt: String): LiveData<Status> {
        val liveData = MutableLiveData<Status>(Status.Loading())
        // TODO: Network req
        val loadWorkoutPlans = Observable.timer(500L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val workoutPlans: Array<WorkoutPlan> = arrayOf(
                        WorkoutPlan(
                            0, userId, "Starting Strength",
                            Period(10, 0), Period(15, 0)
                        )
                    )
                    insertWorkoutPlans(workoutPlans)
                    liveData.value = Status.Success()
                },
                onError = {
                    liveData.value = Status.Error()
                }
            )
        disposables.add(loadWorkoutPlans)
        return liveData
    }

    private fun insertWorkoutPlans(workoutPlans: Array<WorkoutPlan>) {
        val insert = workoutPlanDao.insertWorkoutPlans(workoutPlans)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { e -> Log.e(TAG, e.message, e) }
            )
        disposables.add(insert)
    }

    // TODO: Network req
    fun getWorkoutsOfPlan(workoutPlanId: Int): LiveData<Array<Workout>?> {
        val liveData = MutableLiveData<Array<Workout>?>()
        val getWorkoutsOfPlan = Observable.timer(500L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val workouts = arrayOf(
                        Workout(0, workoutPlanId, 1),
                        Workout(1, workoutPlanId, 2)
                    )
                    liveData.value = workouts
                },
                onError = { e -> Log.e(TAG, e.message, e) }
            )
        disposables.add(getWorkoutsOfPlan)
        return liveData
    }

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
    }
}