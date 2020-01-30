package at.spiceburg.roarfit.data.repositories

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.db.ExerciseDao
import at.spiceburg.roarfit.data.db.ExerciseTemplateDao
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ExerciseRepository(
    private val keyFitApi: KeyFitApi,
    private val exerciseDao: ExerciseDao,
    private val exerciseTemplateDao: ExerciseTemplateDao
) {

    private val disposables = CompositeDisposable()

    // this operation should not be disposable, because it is part of the database initialisation
    @SuppressLint("CheckResult")
    fun insertAllTemplates(templates: List<ExerciseTemplate>): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        exerciseTemplateDao.insertAllTemplates(templates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { liveData.value = true },
                onError = { e -> Log.e(TAG, e.message, e) }
            )
        return liveData
    }

    fun getTemplates(equipment: Equipment): LiveData<List<ExerciseTemplate>> {
        val liveData = MutableLiveData<List<ExerciseTemplate>>()
        val getTemplates = exerciseTemplateDao.getTemplates(equipment)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { exerciseTemplates -> liveData.value = exerciseTemplates },
                onError = { e ->
                    // TODO: display error
                    Log.e(TAG, e.message, e)
                }
            )
        disposables.add(getTemplates)
        return liveData
    }

    fun getAllTemplates(): LiveData<List<ExerciseTemplate>> {
        val liveData = MutableLiveData<List<ExerciseTemplate>>()
        val getAllTemplates = exerciseTemplateDao.getAllTemplates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { exerciseTemplates -> liveData.value = exerciseTemplates },
                onError = { e ->
                    // TODO: display error
                    Log.e(TAG, e.message, e)
                }
            )
        disposables.add(getAllTemplates)
        return liveData
    }

    fun clear() {
        disposables.clear()
    }

    companion object {
        private val TAG = ExerciseRepository::class.java.simpleName
    }
}
