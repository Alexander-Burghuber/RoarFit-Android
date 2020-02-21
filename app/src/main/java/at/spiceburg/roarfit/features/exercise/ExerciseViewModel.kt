package at.spiceburg.roarfit.features.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Response
import at.spiceburg.roarfit.data.repositories.WorkoutRepository

class ExerciseViewModel(private val workoutRepo: WorkoutRepository) : ViewModel() {

    val stopWatch = MutableLiveData<String>("00:00")
    val isStopWatchPaused = MutableLiveData<Boolean>(false)

    fun addPersonalExercise(
        jwt: String, templateId: Int, time: String, sets: Int, reps: Int, weight: String?
    ): LiveData<Response<Unit>> {
        return workoutRepo.addPersonalExercise(jwt, templateId, time, sets, reps, weight)
    }

    fun addWorkoutExercise(
        jwt: String, exerciseId: Int, time: String, sets: Int, reps: Int, weight: String?
    ): LiveData<Response<Unit>> {
        return workoutRepo.addWorkoutExercise(jwt, exerciseId, time, sets, reps, weight)
    }

    override fun onCleared() {
        workoutRepo.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val workoutRepo: WorkoutRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(workoutRepo) as T
        }
    }
}

