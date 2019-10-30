package at.spiceburg.roarfit.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import at.spiceburg.roarfit.data.repositories.ExerciseRepositoryFactory
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val exerciseRepo: ExerciseRepository = ExerciseRepositoryFactory.create(application)

    override fun onCleared() {
        super.onCleared()
        exerciseRepo.clear()
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

    fun initDatabase(): LiveData<Boolean> {
        val inputStream = getApplication<Application>().assets.open("exercises.json")
        val reader = JsonReader(inputStream.reader())
        val exerciseTemplates: Array<ExerciseTemplate> =
            Gson().fromJson(reader, Array<ExerciseTemplate>::class.java)
        return exerciseRepo.insertAllTemplates(exerciseTemplates.toList())
    }
}
