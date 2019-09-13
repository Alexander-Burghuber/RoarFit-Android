package at.htlleonding.roarfit.data.repositories

import android.content.Context
import at.htlleonding.roarfit.data.db.AppDatabase
import at.htlleonding.roarfit.network.KeyFitApiFactory

class ExerciseRepositoryFactory {
    companion object {
        private var exerciseRepository: ExerciseRepository? = null
        fun create(context: Context): ExerciseRepository {
            if (exerciseRepository == null) {
                val keyFitApi = KeyFitApiFactory.create()
                val exerciseDao = AppDatabase.getDatabase(context).exerciseDao()
                exerciseRepository = ExerciseRepository(keyFitApi, exerciseDao)
            }
            return exerciseRepository!!
        }
    }
}