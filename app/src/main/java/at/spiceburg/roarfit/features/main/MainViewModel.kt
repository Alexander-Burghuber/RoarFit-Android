package at.spiceburg.roarfit.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.WorkoutRepository

class MainViewModel(
    userId: Int,
    private val workoutRepo: WorkoutRepository,
    userRepo: UserRepository
) : ViewModel() {

    val user: LiveData<UserDB> = userRepo.getUser(userId)
    private var workoutPlans: LiveData<Response<Array<WorkoutPlan>>>? = null
    private var equipment: LiveData<Response<Array<String>>>? = null

    fun getWorkoutPlans(jwt: String): LiveData<Response<Array<WorkoutPlan>>> {
        if (workoutPlans == null) {
            workoutPlans = workoutRepo.getWorkoutPlan(jwt)
        }
        return workoutPlans!!
    }

    fun getEquipment(jwt: String): LiveData<Response<Array<String>>> {
        if (equipment == null) {
            equipment = workoutRepo.getEquipment(jwt)
        }
        return equipment!!
    }

    fun getExerciseTemplates(
        jwt: String,
        equipment: String
    ): LiveData<Response<Array<ExerciseTemplate>>> {
        return workoutRepo.getExerciseTemplates(jwt, equipment)
    }

    override fun onCleared() {
        workoutRepo.clear()
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
