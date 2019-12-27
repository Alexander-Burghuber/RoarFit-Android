package at.spiceburg.roarfit

import android.content.Context
import at.spiceburg.roarfit.data.db.AppDatabase
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.features.exercise.ExerciseViewModel
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.features.main.exerciselist.ExerciseListViewModel
import at.spiceburg.roarfit.network.KeyFitApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * For manual dependency injection
 * @see <a href="https://developer.android.com/training/dependency-injection/manual">Manual dependency injection</a>
 */
class AppContainer(context: Context) {

    val keyFitApi: KeyFitApi = Retrofit.Builder()
        .baseUrl("https://staging.key.fit/lionsoft/app/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KeyFitApi::class.java)

    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val exerciseDao = AppDatabase.getDatabase(context).exerciseDao()
    var userRepository = UserRepository(keyFitApi, userDao)
    var exerciseRepository = ExerciseRepository(keyFitApi, exerciseDao)

    val mainViewModelFactory = MainViewModel.Factory(exerciseRepository)
    val exerciseViewModelFactory = ExerciseViewModel.Factory(exerciseRepository)
    val exerciseListViewModelFactory = ExerciseListViewModel.Factory(exerciseRepository)
}
