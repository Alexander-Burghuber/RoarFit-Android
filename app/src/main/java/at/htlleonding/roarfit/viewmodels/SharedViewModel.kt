package at.htlleonding.roarfit.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.htlleonding.roarfit.data.Resource
import at.htlleonding.roarfit.data.entities.ExerciseTemplate
import at.htlleonding.roarfit.data.entities.User
import at.htlleonding.roarfit.data.entities.UserExercise
import at.htlleonding.roarfit.data.repositories.ExerciseRepository
import at.htlleonding.roarfit.data.repositories.ExerciseRepositoryFactory
import at.htlleonding.roarfit.data.repositories.UserRepository
import at.htlleonding.roarfit.data.repositories.UserRepositoryFactory
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    var userLD = MutableLiveData<Resource<User>>()
    val exerciseHistoryLD = MutableLiveData<List<UserExercise>>()

    private val userRepo: UserRepository = UserRepositoryFactory.create(application)
    private val exerciseRepo: ExerciseRepository = ExerciseRepositoryFactory.create(application)

    fun getUser(userId: Int, jwt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userLD.postValue(Resource.Loading(userRepo.getUser(userId)))
            userLD.postValue(userRepo.refreshUser(userId, jwt))
        }
    }

    fun addUserExercise() {
        viewModelScope.launch(Dispatchers.IO) {
            val userExercise = UserExercise(
                templateId = 1,
                sets = 5,
                reps = 10,
                groupId = 0
            )
            // exerciseDao.insertUserExercise(userExercise)
        }
    }

    fun loadExerciseHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            // exerciseHistoryLD.postValue(exerciseDao.getAllUserExercises())
        }
    }

    fun initDatabase(): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = getApplication<Application>().assets.open("exercises.json")
            val reader = JsonReader(inputStream.reader())
            val exerciseTemplates: Array<ExerciseTemplate> =
                Gson().fromJson(reader, Array<ExerciseTemplate>::class.java)
            exerciseRepo.insertAllTemplates(exerciseTemplates.toList())
            // Inform the LiveData that the inserting has completed
            liveData.postValue(true)
        }
        return liveData
    }
}