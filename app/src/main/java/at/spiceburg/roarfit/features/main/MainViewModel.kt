package at.spiceburg.roarfit.features.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.WorkoutRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    private val jwt: String,
    private val userRepo: UserRepository,
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private var workoutPlans = MutableLiveData<Result<Array<WorkoutPlan>>>()
    private var equipment = MutableLiveData<Result<Array<String>>>()
    private var user = MutableLiveData<Result<User>>()

    private var disposables = CompositeDisposable()

    fun getWorkoutPlans(): LiveData<Result<Array<WorkoutPlan>>> {
        val res = workoutPlans.value
        if (res == null || res.shouldReload()) {
            loadWorkoutPlans()
        }
        return workoutPlans
    }

    fun getUser(): LiveData<Result<User>> {
        val res = user.value
        if (res == null || res.shouldReload()) {
            loadUser()
        }
        return user
    }

    fun getEquipment(): LiveData<Result<Array<String>>> {
        val res = equipment.value
        if (res == null || res.shouldReload()) {
            loadEquipment()
        }
        return equipment
    }

    fun getExerciseTemplates(equipment: String): LiveData<Result<Array<ExerciseTemplate>>> {
        val liveData = MutableLiveData<Result<Array<ExerciseTemplate>>>(Result.loading())
        val getExerciseTemplates = workoutRepo.getExerciseTemplates(jwt, equipment)
            .subscribeBy(
                onSuccess = { liveData.value = it },
                onError = { Log.e(TAG, "getExerciseTemplates network call error", it) }
            )
        disposables.add(getExerciseTemplates)
        return liveData
    }

    fun getExerciseHistory(count: Int): LiveData<Result<Array<Exercise>>> {
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

    fun loadWorkoutPlans() {
        workoutPlans.value = Result.loading()
        val loadWorkoutPlan = workoutRepo.getWorkoutPlan(jwt).subscribeBy(
            onSuccess = { workoutPlans.value = it },
            onError = { Log.e(TAG, "loadWorkoutPlans network call error", it) }
        )
        disposables.add(loadWorkoutPlan)
    }

    private fun loadUser() {
        user.value = Result.loading()
        val loadUser = userRepo.loadUser(jwt).subscribeBy(
            onSuccess = { user.value = it },
            onError = { Log.e(TAG, "loadUser network call error", it) }
        )
        disposables.add(loadUser)
    }

    private fun loadEquipment() {
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
        private val jwt: String,
        private val userRepo: UserRepository,
        private val workoutRepo: WorkoutRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(jwt, userRepo, workoutRepo) as T
        }
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}
