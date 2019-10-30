package at.spiceburg.roarfit.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.db.ExerciseDao
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.network.KeyFitApi
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
}
