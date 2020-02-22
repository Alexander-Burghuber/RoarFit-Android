package at.spiceburg.roarfit.data.repositories

import androidx.lifecycle.LiveData
import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.NetworkError
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.db.Dao
import at.spiceburg.roarfit.data.db.UserDB
import at.spiceburg.roarfit.data.dto.LoginRequest
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class UserRepository(private val keyFitApi: KeyFitApi, private val dao: Dao) : DefaultRepository() {

    fun getUser(userId: Int): LiveData<UserDB> = dao.getUser(userId)

    fun login(username: String, password: String): Single<Result<LoginData>> {
        return keyFitApi.login(LoginRequest(username, password))
            .toResult()
            .onErrorResumeNext { e ->
                val res: Result<LoginData> = if (e is HttpException && e.code() == 401) {
                    Result.failure(NetworkError.USERNAME_PASSWORD_WRONG)
                } else {
                    handleDefaultNetworkErrors(e)
                }
                Single.just(res)
            }
    }

    fun loadUser(jwt: String): Single<Result<UserDB>> {
        return keyFitApi.getUser(getJwtString(jwt))
            .toResult()
    }

    fun insertUser(user: UserDB): Completable {
        return dao.insertUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}
