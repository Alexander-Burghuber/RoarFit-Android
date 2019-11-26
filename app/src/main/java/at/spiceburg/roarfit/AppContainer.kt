package at.spiceburg.roarfit

import android.content.Context
import at.spiceburg.roarfit.data.db.AppDatabase
import at.spiceburg.roarfit.data.repositories.ExerciseRepository
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.features.main.exercise.ExerciseViewModel
import at.spiceburg.roarfit.features.main.exerciselist.ExerciseListViewModel
import at.spiceburg.roarfit.features.main.profile.ProfileViewModel
import at.spiceburg.roarfit.network.KeyFitApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val keyFitApi = Retrofit.Builder()
        .baseUrl("https://staging.key.fit/lionsoft/app/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KeyFitApi::class.java)
    private val userDao = AppDatabase.getDatabase(context).userDao()
    private val exerciseDao = AppDatabase.getDatabase(context).exerciseDao()

    private var userRepository = UserRepository(keyFitApi, userDao)
    private var exerciseRepository = ExerciseRepository(keyFitApi, exerciseDao)

    val mainViewModelFactory = MainViewModel.Factory(exerciseRepository)
    val exerciseViewModelFactory = ExerciseViewModel.Factory(exerciseRepository)
    val exerciseListViewModelFactory = ExerciseListViewModel.Factory(exerciseRepository)
    val profileViewModelFactory = ProfileViewModel.Factory(userRepository)
}
