package at.spiceburg.roarfit.features.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ExerciseViewModel : ViewModel() {

    val stopWatch = MutableLiveData<String>("00:00")
    val isStopWatchPaused = MutableLiveData<Boolean>(false)

    @Suppress("UNCHECKED_CAST")
    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel() as T
        }
    }
}

