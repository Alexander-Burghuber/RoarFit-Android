package at.spiceburg.roarfit.ui.workout.exercise

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import java.text.SimpleDateFormat
import java.util.*

class ExerciseViewModel(private val exerciseRepo: ExerciseRepository) : ViewModel() {
    val stopWatch = MutableLiveData<String>()
    private val timer = Timer()
    private var isTimerRunning = false
    private val startTime = System.currentTimeMillis()
    private val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)

    fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    stopWatch.postValue(formatter.format(Date().time - startTime))
                }
            }, 0, 1000)
        }
    }

    fun pauseTimer() {

    }

    fun clearTimer() {
        timer.cancel()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(
                ExerciseRepository.Factory.create(
                    context
                )
            ) as T
        }
    }
}

