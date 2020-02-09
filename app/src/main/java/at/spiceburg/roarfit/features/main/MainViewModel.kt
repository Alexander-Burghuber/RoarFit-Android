package at.spiceburg.roarfit.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.entities.UserExercise
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.WorkoutRepository

class MainViewModel(
    userId: Int,
    private val workoutRepo: WorkoutRepository,
    userRepo: UserRepository
) : ViewModel() {

    val user: LiveData<UserDB> = userRepo.getUser(userId)

    fun getWorkoutPlans(jwt: String): LiveData<Response<WorkoutPlan>> {
        return workoutRepo.getWorkoutPlan(jwt)
    }

    fun getExercisesOfWorkout(jwt: String, workoutId: Int): LiveData<Response<List<UserExercise>>> {
        return workoutRepo.getExercisesOfWorkout(jwt, workoutId)
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
}
