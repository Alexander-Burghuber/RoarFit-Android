package at.spiceburg.roarfit.features.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ExerciseViewModel() : ViewModel() {

    val time = MutableLiveData<String>("00:00")

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel() as T
        }
    }
}

