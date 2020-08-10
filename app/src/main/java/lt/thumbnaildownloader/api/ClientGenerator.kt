package lt.thumbnaildownloader.api

import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ClientGenerator {

    companion object {

        private val dispatcher = Dispatcher()

        private val httpClient = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)

        private var retrofit: Retrofit? = null

        fun <S> createService(serviceClass: Class<S>?): S {

            dispatcher.maxRequests = 1
            dispatcher.maxRequestsPerHost = 1

            httpClient.dispatcher(dispatcher)
            val baseUrl: String = "https://www.googleapis.com/youtube/v3/"
            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
            builder.client(httpClient.build())
            retrofit = builder.build()

            return retrofit!!.create(serviceClass)
        }
    }

}