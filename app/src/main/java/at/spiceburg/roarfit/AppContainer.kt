package at.spiceburg.roarfit

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
class AppContainer {

    val userRepository: UserRepository
    val workoutRepository: WorkoutRepository

    init {
        val keyFitApi: KeyFitApi = Retrofit.Builder()
            //.baseUrl("https://staging.key.fit/lionsoft/app/")
            .baseUrl("https://vm102.htl-leonding.ac.at/roarfit/member/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KeyFitApi::class.java)

        userRepository = UserRepository(keyFitApi)
        workoutRepository = WorkoutRepository(keyFitApi)
    }
}
