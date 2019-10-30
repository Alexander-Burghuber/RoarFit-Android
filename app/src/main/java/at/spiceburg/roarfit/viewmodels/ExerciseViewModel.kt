package at.spiceburg.roarfit.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import at.spiceburg.roarfit.data.repositories.ExerciseRepositoryFactory
import java.text.SimpleDateFormat
import java.util.*

class ExerciseViewModel(private val exerciseRepo: ExerciseRepository) : ViewModel() {
    val timerLD = MutableLiveData<String>()
    private val timer = Timer()
    private var isTimerRunning = false

    fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true

            timer.scheduleAtFixedRate(object : TimerTask() {
                val startTime = System.currentTimeMillis()
                val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)
                override fun run() {
                    timerLD.postValue(formatter.format(Date().time - startTime))
                }
            }, 0, 1000)
        }
    }

    fun stopTimer() {
        timer.cancel()
    }

    class ExerciseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(ExerciseRepositoryFactory.create(context)) as T
        }
    }
}
