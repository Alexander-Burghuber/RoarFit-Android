package at.spiceburg.roarfit.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class KeyFitApiFactory {
    companion object {
        private var keyFitApi: KeyFitApi? = null
        fun create(): KeyFitApi {
            if (keyFitApi == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://staging.key.fit/lionsoft/app/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                keyFitApi = retrofit.create(KeyFitApi::class.java)
            }
            return keyFitApi!!
        }
    }
}
