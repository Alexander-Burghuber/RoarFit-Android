package at.spiceburg.roarfit.features.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import at.spiceburg.roarfit.data.repositories.UserRepository
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader

class MainViewModel(
    private val userId: Int,
    private val userRepo: UserRepository,
    private val exerciseRepo: ExerciseRepository
) : ViewModel() {

    val user: LiveData<User> = userRepo.getUser(userId)
    val workoutPlans: LiveData<Array<WorkoutPlan>?> = userRepo.getWorkoutPlans(userId)

    fun loadWorkoutPlans(jwt: String): LiveData<Status> {
        return userRepo.loadWorkoutPlans(userId, jwt)
    }

    fun getAllExerciseTemplates(): LiveData<List<ExerciseTemplate>> {
        return exerciseRepo.getAllTemplates()
    }

    fun initDatabase(context: Context): LiveData<Boolean> {
        val inputStream: InputStream = context.assets.open("exercises.json")
        val exerciseTemplates: Array<ExerciseTemplate> =
            Gson().fromJson(InputStreamReader(inputStream), Array<ExerciseTemplate>::class.java)
        return exerciseRepo.insertAllTemplates(exerciseTemplates.toList())
    }

    override fun onCleared() {
        exerciseRepo.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userId: Int,
        private val userRep: UserRepository,
        private val exerciseRepo: ExerciseRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(userId, userRep, exerciseRepo) as T
        }
    }
}
