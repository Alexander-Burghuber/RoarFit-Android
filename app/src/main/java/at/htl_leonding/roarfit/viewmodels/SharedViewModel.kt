package at.htl_leonding.roarfit.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.htl_leonding.roarfit.data.AppDatabase
import at.htl_leonding.roarfit.data.ExerciseDao
import at.htl_leonding.roarfit.data.entities.ExerciseTemplate
import at.htl_leonding.roarfit.data.entities.User
import at.htl_leonding.roarfit.data.entities.UserExercise
import at.htl_leonding.roarfit.network.KeyFitApi
import at.htl_leonding.roarfit.network.KeyFitApiFactory
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    val userLD = MutableLiveData<Result<User>>()
    val exerciseHistoryLD = MutableLiveData<List<UserExercise>>()

    private val keyFitApi: KeyFitApi = KeyFitApiFactory.create()
    private val exerciseDao: ExerciseDao =
        AppDatabase.getDatabase(application).exerciseDao()

    fun loadUser(jwt: String, customerNum: Int) {
        viewModelScope.launch {
            try {
                val response: Response<User> = keyFitApi.getUser(customerNum, "Bearer $jwt")
                if (response.isSuccessful) {
                    userLD.value = Result.success(response.body()!!)
                } else {
                    val msg = when (response.code()) {
                        401 -> "The authorization token has expired."
                        404 -> "The entered customer number is not associated with an user."
                        else -> "An unexpected error occurred."
                    }
                    userLD.value = Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                val msg = "An unknown error occurred"
                Log.e("SharedViewModel", msg, e)
                userLD.value = Result.failure(Exception(msg))
            }
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
            exerciseDao.insertUserExercise(userExercise)
        }
    }

    fun loadExerciseHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseHistoryLD.postValue(exerciseDao.getAllUserExercises())
        }
    }

    fun initDatabase(): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = getApplication<Application>().assets.open("exercises.json")
            val reader = JsonReader(inputStream.reader())
            val exerciseTemplates: Array<ExerciseTemplate> =
                Gson().fromJson(reader, Array<ExerciseTemplate>::class.java)
            exerciseDao.insertAllTemplates(exerciseTemplates.toList())
            // Inform the LiveData that the inserting has completed
            liveData.postValue(true)
        }
        return liveData
    }
}