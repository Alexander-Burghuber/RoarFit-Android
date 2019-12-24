package at.spiceburg.roarfit.features.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.repositories.ExerciseRepository

class ExerciseViewModel(private val exerciseRepo: ExerciseRepository) : ViewModel() {

    class Factory(private val exerciseRepo: ExerciseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseViewModel(
                exerciseRepo
            ) as T
        }
    }
}

