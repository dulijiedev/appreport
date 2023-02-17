package com.roobo.appreport.api

import com.roobo.appreport.networklibrary.base.NetworkApi
import com.roobo.appreport.networklibrary.baseUrl

class CommonNetApi : NetworkApi() {

    companion object {
        val sInstance: CommonNetApi by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CommonNetApi()
        }
    }

    fun <T> getService(service: Class<T>, BASE_URL: String = baseUrl): T {
        return sInstance.getRetrofit(service, BASE_URL).create(service)
    }

}