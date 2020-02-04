package at.spiceburg.roarfit.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Period
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.db.dao.WorkoutExerciseDao
import at.spiceburg.roarfit.data.db.entities.UserExercise
import at.spiceburg.roarfit.data.db.entities.Workout
import at.spiceburg.roarfit.data.db.entities.WorkoutPlan
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class WorkoutRepository(
    private val keyFitApi: KeyFitApi,
    private val dao: WorkoutExerciseDao
) {

    private val disposables = CompositeDisposable()

    fun getWorkoutPlans(userId: Int) = dao.getWorkoutPlanWithWorkouts(userId)

    // TODO: Network req
    fun loadWorkoutPlans(userId: Int, jwt: String): LiveData<Status> {
        val liveData = MutableLiveData<Status>(Status.Loading())
        val loadWorkoutPlans = Observable.timer(500L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val workoutPlan = WorkoutPlan(
                        0, userId, "Starting Strength",
                        Period(10, 0), Period(15, 0)
                    )
                    insertWorkoutPlan(workoutPlan)
                    val workouts: List<Workout> = listOf(
                        Workout(0, workoutPlan.id, 1),
                        Workout(1, workoutPlan.id, 2)
                    )
                    liveData.value = Status.Success()
                },
                onError = {
                    liveData.value = Status.Error()
                }
            )
        disposables.add(loadWorkoutPlans)
        return liveData
    }

    private fun insertWorkoutPlan(workoutPlan: WorkoutPlan) {
        val insert = dao.insertWorkoutPlan(workoutPlan)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { e -> Log.e(TAG, e.message, e) }
            )
        disposables.add(insert)
    }

    /*
    private fun insertWorkouts(workouts: Array<Workout>) {
        val insert = dao.insertWorkoutPlans(workoutPlans)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { e -> Log.e(TAG, e.message, e) }
            )
        disposables.add(insert)
    }*/

    fun getExercisesOfWorkout(workoutId: Int): LiveData<Array<UserExercise>?> {
        val liveData = MutableLiveData<Array<UserExercise>?>()
        val getExercisesOfWorkout = Observable.timer(500L, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val userExercises = arrayOf(
                        UserExercise(0, 3, workoutId, 4, 10, 0)
                    )
                    liveData.value = userExercises
                },
                onError = { e -> Log.e(TAG, e.message, e) }
            )
        disposables.add(getExercisesOfWorkout)
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
    }
}