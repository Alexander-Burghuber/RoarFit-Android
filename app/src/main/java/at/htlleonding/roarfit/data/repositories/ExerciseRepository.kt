package at.htlleonding.roarfit.data.repositories

import androidx.annotation.WorkerThread
import at.htlleonding.roarfit.data.db.ExerciseDao
import at.htlleonding.roarfit.data.entities.ExerciseTemplate
import at.htlleonding.roarfit.network.KeyFitApi

class ExerciseRepository(private val keyFitApi: KeyFitApi, private val exerciseDao: ExerciseDao) {

    @WorkerThread
    suspend fun insertAllTemplates(templates: List<ExerciseTemplate>) {
        exerciseDao.insertAllTemplates(templates)
    }
}