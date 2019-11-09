package at.spiceburg.roarfit.ui.workout.exerciselist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.repositories.ExerciseRepository

class ExerciseListViewModel(private val exerciseRepo: ExerciseRepository) : ViewModel() {

    fun getExerciseTemplates(equipment: Equipment): LiveData<List<ExerciseTemplate>> {
        return exerciseRepo.getTemplates(equipment)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ExerciseListViewModel(ExerciseRepository.Factory.create(context)) as T
        }
    }
}