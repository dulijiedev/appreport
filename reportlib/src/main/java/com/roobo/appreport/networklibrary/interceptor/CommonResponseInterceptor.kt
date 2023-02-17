package com.roobo.appreport.networklibrary.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class CommonResponseInterceptor :Interceptor{
   private val TAG: String = "ResponseInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        var requestTime: Long = System.currentTimeMillis()

        val response: Response = chain.proceed(chain.request())
        val maxAge = 60 * 60
        response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        Log.d(TAG, "requestTime: ${System.currentTimeMillis() - requestTime}")
        return response
    }

}