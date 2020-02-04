package at.spiceburg.roarfit.features.main.exerciselist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.db.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.repositories.ExerciseRepository

class ExerciseListViewModel(private val exerciseRepo: ExerciseRepository) : ViewModel() {

    fun getExerciseTemplates(equipment: Equipment): LiveData<List<ExerciseTemplate>> {
        return exerciseRepo.getTemplates(equipment)
    }

    class Factory(private val exerciseRepo: ExerciseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseListViewModel(exerciseRepo) as T
        }
    }
}