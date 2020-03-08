package at.spiceburg.roarfit.features.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.WorkoutRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    userId: Int,
    private val workoutRepo: WorkoutRepository,
    userRepo: UserRepository
) : ViewModel() {

    val user: LiveData<UserDB> = userRepo.getUser(userId)
    private var workoutPlans = MutableLiveData<Result<Array<WorkoutPlan>>>()
    private var equipment = MutableLiveData<Result<Array<String>>>()

    private var disposables = CompositeDisposable()

    fun getWorkoutPlans(jwt: String): LiveData<Result<Array<WorkoutPlan>>> {
        val res = workoutPlans.value
        if (res == null || res.shouldReload()) {
            loadWorkoutPlans(jwt)
        }
        return workoutPlans
    }

    fun getEquipment(jwt: String): LiveData<Result<Array<String>>> {
        val res = equipment.value
        if (res == null || res.shouldReload()) {
            loadEquipment(jwt)
        }
        return equipment
    }

    fun getExerciseTemplates(
        jwt: String,
        equipment: String
    ): LiveData<Result<Array<ExerciseTemplate>>> {
        val liveData = MutableLiveData<Result<Array<ExerciseTemplate>>>(Result.loading())
        val getExerciseTemplates = workoutRepo.getExerciseTemplates(jwt, equipment)
            .subscribeBy(
                onSuccess = { liveData.value = it },
                onError = { Log.e(TAG, "getExerciseTemplates network call error", it) }
            )
        disposables.add(getExerciseTemplates)
        return liveData
    }

    fun getExerciseHistory(jwt: String, count: Int): LiveData<Result<Array<Exercise>>> {
        val liveData = MutableLiveData<Result<Array<Exercise>>>(Result.loading())
        val loadExerciseHistory = workoutRepo.getExerciseHistory(jwt, count).subscribeBy(
            onSuccess = { liveData.value = it },
            onError = { Log.e(TAG, "loadExerciseHistory network call error", it) }
        )
        disposables.add(loadExerciseHistory)
        return liveData
    }

    /*fun getTimeSpentStatistic(jwt: String) {
        val loadExerciseHistory = workoutRepo.getExerciseHistory(jwt, 0)
            .map { res ->
                if (res.isSuccess()) {
                    res.data?.forEach { exercise ->
                        exercise.completedDate?.let {
                            val date = Date(it)
                            val calendar = Calendar.getInstance()
                            calendar.time = date
                            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                                Calendar.MONDAY -> {
                                    "MO"
                                }
                                Calendar.TUESDAY -> {

                                }
                                Calendar.WEDNESDAY -> {

                                }
                                Calendar.THURSDAY -> {

                                }
                                Calendar.FRIDAY -> {

                                }
                                Calendar.SATURDAY -> {

                                }
                                Calendar.SUNDAY -> {

                                }
                            }
                        }
                    }
                }
            }
        disposables.add(loadExerciseHistory)
    }*/

    fun loadWorkoutPlans(jwt: String) {
        workoutPlans.value = Result.loading()
        val loadWorkoutPlan = workoutRepo.getWorkoutPlan(jwt).subscribeBy(
            onSuccess = { workoutPlans.value = it },
            onError = { Log.e(TAG, "loadWorkoutPlans network call error", it) }
        )
        disposables.add(loadWorkoutPlan)
    }

    private fun loadEquipment(jwt: String) {
        equipment.value = Result.loading()
        val loadEquipment = workoutRepo.getEquipment(jwt).subscribeBy(
            onSuccess = { equipment.value = it },
            onError = { Log.e(TAG, "loadEquipment network call error", it) }
        )
        disposables.add(loadEquipment)
    }

    override fun onCleared() {
        disposables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userId: Int,
        private val userRepo: UserRepository,
        private val workoutRepo: WorkoutRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(userId, workoutRepo, userRepo) as T
        }
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}
