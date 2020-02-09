package at.spiceburg.roarfit.features.main.exerciselist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.entities.ExerciseTemplate

class ExerciseListViewModel() : ViewModel() {

    fun getExerciseTemplates(equipment: Equipment): LiveData<List<ExerciseTemplate>>? {
        return null
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseListViewModel() as T
        }
    }
}