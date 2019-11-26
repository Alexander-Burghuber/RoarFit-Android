package at.spiceburg.roarfit.features.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

class MainViewModel(private val exerciseRepo: ExerciseRepository) : ViewModel() {

    override fun onCleared() {
        super.onCleared()
        exerciseRepo.clear()
    }

    fun getAllExerciseTemplates(): LiveData<List<ExerciseTemplate>> {
        return exerciseRepo.getAllTemplates()
    }

    /* fun addUserExercise() {
         viewModelScope.launch(Dispatchers.IO) {
             val userExercise = UserExercise(
                 templateId = 1,
                 sets = 5,
                 reps = 10,
                 groupId = 0
             )
             // exerciseDao.insertUserExercise(userExercise)
         }
     }*/

    fun initDatabase(context: Context): LiveData<Boolean> {
        val inputStream = context.assets.open("exercises.json")
        val reader = JsonReader(inputStream.reader())
        val exerciseTemplates: Array<ExerciseTemplate> =
            Gson().fromJson(reader, Array<ExerciseTemplate>::class.java)
        return exerciseRepo.insertAllTemplates(exerciseTemplates.toList())
    }

    class Factory(private val exerciseRepo: ExerciseRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(exerciseRepo) as T
        }
    }
}
