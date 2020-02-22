package at.spiceburg.roarfit.features.exercise

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.dto.PersonalExerciseDTO
import at.spiceburg.roarfit.data.dto.WorkoutExerciseDTO
import at.spiceburg.roarfit.data.repositories.WorkoutRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class ExerciseViewModel(private val workoutRepo: WorkoutRepository) : ViewModel() {

    val stopWatch = MutableLiveData<String>("00:00")
    val isStopWatchPaused = MutableLiveData<Boolean>(false)

    private val disposables = CompositeDisposable()

    fun addPersonalExercise(
        jwt: String,
        personalExerciseDTO: PersonalExerciseDTO
    ): LiveData<Result<Unit>> {
        val liveData = MutableLiveData<Result<Unit>>(Result.loading())
        val addPersonalExercise = workoutRepo.addPersonalExercise(jwt, personalExerciseDTO)
            .subscribeBy(
                onSuccess = { liveData.value = it },
                onError = { Log.e(TAG, "addPersonalExercise network call error", it) }
            )
        disposables.add(addPersonalExercise)
        return liveData
    }

    fun addWorkoutExercise(
        jwt: String,
        workoutExerciseDTO: WorkoutExerciseDTO
    ): LiveData<Result<Unit>> {
        val liveData = MutableLiveData<Result<Unit>>(Result.loading())
        val addWorkoutExercise = workoutRepo.addWorkoutExercise(jwt, workoutExerciseDTO)
            .subscribeBy(
                onSuccess = { liveData.value = it },
                onError = { Log.e(TAG, "addWorkoutExercise network call error", it) }
            )
        disposables.add(addWorkoutExercise)
        return liveData
    }

    override fun onCleared() {
        disposables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val workoutRepo: WorkoutRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(workoutRepo) as T
        }
    }

    companion object {
        private val TAG = ExerciseViewModel::class.java.simpleName
    }
}

