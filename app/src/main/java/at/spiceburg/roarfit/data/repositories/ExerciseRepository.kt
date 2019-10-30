package at.spiceburg.roarfit.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.db.AppDatabase
import at.spiceburg.roarfit.data.db.ExerciseDao
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.network.KeyFitApi
import at.spiceburg.roarfit.network.KeyFitApiFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ExerciseRepository(private val keyFitApi: KeyFitApi, private val exerciseDao: ExerciseDao) {
    private val disposables = CompositeDisposable()

    fun insertAllTemplates(templates: List<ExerciseTemplate>): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        disposables.add(exerciseDao.insertAllTemplates(templates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { liveData.value = true }
        )
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    class Factory {
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
}
