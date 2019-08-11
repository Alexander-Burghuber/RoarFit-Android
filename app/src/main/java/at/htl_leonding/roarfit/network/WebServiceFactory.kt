package at.htl_leonding.roarfit.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WebServiceFactory {
    companion object {
        private var webService: WebService? = null
        fun create(): WebService {
            if (webService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://staging.key.fit/lionsoft/app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                webService = retrofit.create(WebService::class.java)
            }
            return webService!!
        }
    }
}