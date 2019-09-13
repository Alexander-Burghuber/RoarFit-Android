package at.htl_leonding.roarfit.data.repositories

import androidx.annotation.WorkerThread
import at.htl_leonding.roarfit.data.ExerciseDao
import at.htl_leonding.roarfit.data.entities.ExerciseTemplate
import at.htl_leonding.roarfit.network.KeyFitApi

class ExerciseRepository(private val keyFitApi: KeyFitApi, private val exerciseDao: ExerciseDao) {

    @WorkerThread
    suspend fun insetAllTemplates(templates: List<ExerciseTemplate>) {
        exerciseDao.insertAllTemplates(templates)
    }
}