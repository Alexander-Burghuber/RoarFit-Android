package at.spiceburg.roarfit.data.repositories

import android.content.Context
import at.spiceburg.roarfit.data.db.AppDatabase
import at.spiceburg.roarfit.network.KeyFitApiFactory

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
