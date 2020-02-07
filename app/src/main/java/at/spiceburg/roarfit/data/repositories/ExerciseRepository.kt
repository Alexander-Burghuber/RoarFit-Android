package at.spiceburg.roarfit.data.repositories

import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.disposables.CompositeDisposable

class ExerciseRepository(private val keyFitApi: KeyFitApi) {

    private val disposables = CompositeDisposable()

    fun clear() {
        disposables.clear()
    }

    companion object {
        private val TAG = ExerciseRepository::class.java.simpleName
    }
}
