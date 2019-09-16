package at.spiceburg.roarfit.data.repositories

import androidx.annotation.WorkerThread
import at.spiceburg.roarfit.data.db.ExerciseDao
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.network.KeyFitApi

class ExerciseRepository(private val keyFitApi: KeyFitApi, private val exerciseDao: ExerciseDao) {

    @WorkerThread
    suspend fun insertAllTemplates(templates: List<ExerciseTemplate>) {
        exerciseDao.insertAllTemplates(templates)
    }
}