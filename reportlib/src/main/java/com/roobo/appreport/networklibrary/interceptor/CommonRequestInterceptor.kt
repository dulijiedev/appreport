package com.roobo.appreport.networklibrary.interceptor

import com.roobo.appreport.networklibrary.base.INetworkRequiredInfo
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 通用参数
 */
class CommonRequestInterceptor(val requireInfo: INetworkRequiredInfo) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        builder.addHeader("Authorization", requireInfo.getToken())
        return chain.proceed(builder.build())
    }

}