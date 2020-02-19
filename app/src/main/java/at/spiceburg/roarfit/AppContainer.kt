package at.spiceburg.roarfit

import android.content.Context
import at.spiceburg.roarfit.data.db.AppDatabase
import at.spiceburg.roarfit.data.repositories.UserRepository
import at.spiceburg.roarfit.data.repositories.WorkoutRepository
import at.spiceburg.roarfit.network.KeyFitApi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * For manual dependency injection
 * @see <a href="https://developer.android.com/training/dependency-injection/manual">Manual dependency injection</a>
 */
class AppContainer(context: Context) {

    private val keyFitApi: KeyFitApi = Retrofit.Builder()
        //.baseUrl("https://staging.key.fit/lionsoft/app/")
        //.baseUrl("https://vm102.htl-leonding.ac.at/roarfit/")
        .baseUrl("http://192.168.0.142:8181/roarfit/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(KeyFitApi::class.java)

    var userRepository: UserRepository
    var workoutRepository: WorkoutRepository

    init {
        val database = AppDatabase.getDatabase(context)
        val dao = database.dao()

        userRepository = UserRepository(keyFitApi, dao)
        workoutRepository = WorkoutRepository(keyFitApi)
    }
}
