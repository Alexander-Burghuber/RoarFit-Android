package at.spiceburg.roarfit.features.main.statistics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.entities.Exercise
import at.spiceburg.roarfit.data.repositories.WorkoutRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewModel(
    private val jwt: String,
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private var exercises = MutableLiveData<Result<Array<Exercise>>>(Result.loading())
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private var disposables = CompositeDisposable()

    fun getExercisesOfWeek(): LiveData<Result<Array<Exercise>>> {
        return exercises
    }

    fun loadExercisesOfWeek(date: Date) {
        exercises.value = Result.loading()
        val loadWorkoutPlan =
            workoutRepo.getExercisesOfWeek(jwt, formatter.format(date)).subscribeBy(
                onSuccess = { exercises.value = it },
                onError = { Log.e(TAG, "loadWorkoutPlans network call error", it) }
            )
        disposables.add(loadWorkoutPlan)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val jwt: String,
        private val workoutRepo: WorkoutRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return StatisticsViewModel(jwt, workoutRepo) as T
        }
    }

    companion object {
        private val TAG = StatisticsViewModel::class.java.simpleName
    }
}