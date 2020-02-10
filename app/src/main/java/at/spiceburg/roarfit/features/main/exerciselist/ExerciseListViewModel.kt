package at.spiceburg.roarfit.features.main.exerciselist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ExerciseListViewModel() : ViewModel() {

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseListViewModel() as T
        }
    }
}