package at.spiceburg.roarfit.features.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.spiceburg.roarfit.data.Status
import at.spiceburg.roarfit.data.entities.*
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.WorkoutRepository
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader

class MainViewModel(
    private val userId: Int,
    private val userRepo: UserRepository,
    private val exerciseRepo: ExerciseRepository,
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    val user: LiveData<User> = userRepo.getUser(userId)
    val workoutPlans: LiveData<Array<WorkoutPlan>?> = workoutRepo.getWorkoutPlans(userId)

    fun loadWorkoutPlans(jwt: String): LiveData<Status> {
        return workoutRepo.loadWorkoutPlans(userId, jwt)
    }

    fun getWorkoutsOfPlan(workoutPlanId: Int): LiveData<Array<Workout>?> {
        return workoutRepo.getWorkoutsOfPlan(workoutPlanId)
    }

    fun getExercisesOfWorkout(workoutId: Int): LiveData<Array<UserExercise>?> {
        return workoutRepo.getExercisesOfWorkout(workoutId)
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
        private val exerciseRepo: ExerciseRepository,
        private val workoutRepo: WorkoutRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(userId, userRep, exerciseRepo, workoutRepo) as T
        }
    }
}
