package com.roobo.appreport.networklibrary.base

import com.roobo.appreport.networklibrary.interceptor.CommonRequestInterceptor
import com.roobo.appreport.networklibrary.interceptor.CommonResponseInterceptor
import com.roobo.appreport.networklibrary.interceptor.HttpLogger
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit



open abstract class NetworkApi {

    private val retrofitHashMap: HashMap<String, Retrofit> = HashMap()
    private var okHttpClient: OkHttpClient? = null

    companion object {
        private const val CACHE_SIZE: Long = 100 * 1024 * 1024 //缓存大小
        private const val READ_TIME_OUT: Long = 10 //读超时时间
        private const val CONNECTION_TIME_OUT: Long = 10 //链接超时时间
        private lateinit var iNetworkRequiredInfo: INetworkRequiredInfo
        fun initData(networkRequiredInfo: INetworkRequiredInfo) {
            iNetworkRequiredInfo = networkRequiredInfo
        }
    }

    fun <T> getRetrofit(serviceClass: Class<T>, mBaseUrl: String): Retrofit {
        if (retrofitHashMap[mBaseUrl + serviceClass.name] != null) {
            return retrofitHashMap[mBaseUrl + serviceClass.name]!!
        }
        val retrofitBuilder = Retrofit.Builder().apply {
            baseUrl(mBaseUrl)
            client(getOkHttpClient())
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
        val retrofit = retrofitBuilder.build()
        retrofitHashMap[mBaseUrl + serviceClass.name] = retrofit
        return retrofit
    }

    fun <T> getScalarsApiService(serviceClass: Class<T>, mBaseUrl: String): Retrofit {
        if (retrofitHashMap[mBaseUrl + serviceClass.name] != null) {
            return retrofitHashMap[mBaseUrl + serviceClass.name]!!
        }
        val retrofitBuilder = Retrofit.Builder().apply {
            baseUrl(mBaseUrl)
            client(getOkHttpClient())
            addConverterFactory(ScalarsConverterFactory.create())
        }
        val retrofit = retrofitBuilder.build()
        retrofitHashMap[mBaseUrl + serviceClass.name] = retrofit
        return retrofit
    }

    private fun getOkHttpClient(): OkHttpClient? {
        if (okHttpClient != null) {
            return okHttpClient
        }
        val okHttpClientBuilder = OkHttpClient.Builder()
        //自定义拦截器
        interceptors()?.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }

        //设置缓存
        okHttpClientBuilder.cache(
            Cache(
                iNetworkRequiredInfo.getApplicationContext().cacheDir,
                CACHE_SIZE
            )
        )
        okHttpClientBuilder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
        okHttpClientBuilder.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)

        okHttpClientBuilder.addInterceptor(CommonRequestInterceptor(iNetworkRequiredInfo))
        okHttpClientBuilder.addInterceptor(CommonResponseInterceptor())

        //在debug情况下打印日志
        if (iNetworkRequiredInfo.isDebug()) {
            /*val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(loggingInterceptor)*/
            val logInterceptor = HttpLoggingInterceptor(HttpLogger())
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(logInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    open fun interceptors(): List<Interceptor>? {
        return null
    }

}