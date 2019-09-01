package at.htl_leonding.roarfit.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class OngoingExerciseViewModel : ViewModel() {
    val timerLiveData = MutableLiveData<String>()
    private val timer = Timer()
    private var isTimerRunning = false

    fun startTimer() {
        if (!isTimerRunning) {
            isTimerRunning = true

            timer.scheduleAtFixedRate(object : TimerTask() {
                val startTime = System.currentTimeMillis()
                val formatter = SimpleDateFormat("mm:ss", Locale.ENGLISH)
                override fun run() {
                    timerLiveData.postValue(formatter.format(Date().time - startTime))
                }
            }, 0, 1000)
        }
    }

    fun stopTimer() {
        timer.cancel()
    }
}