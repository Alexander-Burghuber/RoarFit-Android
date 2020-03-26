package at.spiceburg.roarfit.features.main.statistics

import android.util.Log
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

    val calendar: MutableLiveData<Calendar>
    val exercises = MutableLiveData<Result<Array<Exercise>>>(Result.loading())

    private val formatter = SimpleDateFormat("yyyy-MM", Locale.US)
    private var disposables = CompositeDisposable()

    init {
        /* setup calendar with only year and month because simply using Calendar.getInstance()
          causes problems when switching the month */
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        this.calendar = MutableLiveData(calendar)
    }

    fun loadExercisesOfMonth(date: Date) {
        exercises.value = Result.loading()
        val loadWorkoutPlan =
            workoutRepo.getExercisesOfMonth(jwt, formatter.format(date)).subscribeBy(
                onSuccess = { exercises.value = it },
                onError = { Log.e(TAG, "loadWorkoutPlans network call error", it) }
            )
        disposables.add(loadWorkoutPlan)
    }

    fun updateCalendarMonth(amount: Int) {
        val calendar: Calendar? = this.calendar.value
        calendar?.add(Calendar.MONTH, amount)
        this.calendar.value = calendar
    }

    override fun onCleared() {
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