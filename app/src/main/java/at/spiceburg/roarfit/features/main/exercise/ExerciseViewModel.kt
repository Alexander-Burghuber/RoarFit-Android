package at.spiceburg.roarfit.features.main.exercise

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

    class Factory(private val exerciseRepo: ExerciseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(exerciseRepo) as T
        }
    }
}

