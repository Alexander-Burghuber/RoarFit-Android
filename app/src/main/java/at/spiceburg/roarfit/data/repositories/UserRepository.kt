package at.spiceburg.roarfit.data.repositories

import at.spiceburg.roarfit.data.LoginData
import at.spiceburg.roarfit.data.NetworkError
import at.spiceburg.roarfit.data.Result
import at.spiceburg.roarfit.data.dto.LoginRequest
import at.spiceburg.roarfit.data.entities.User
import at.spiceburg.roarfit.network.KeyFitApi
import io.reactivex.Single
import retrofit2.HttpException

class UserRepository(private val keyFitApi: KeyFitApi) : DefaultRepository() {

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

    fun loadUser(jwt: String): Single<Result<User>> {
        return keyFitApi.getUser(getJwtString(jwt))
            .toResult()
            .onErrorResumeNext { Single.just(handleDefaultNetworkErrors(it)) }
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}
