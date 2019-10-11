package at.spiceburg.roarfit.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.repositories.ExerciseRepositoryFactory

class ExerciseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExerciseViewModel(ExerciseRepositoryFactory.create(context)) as T
    }
}
