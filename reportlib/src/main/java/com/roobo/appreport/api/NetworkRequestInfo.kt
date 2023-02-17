package com.roobo.appreport.api

import android.app.Application
import com.roobo.appreport.BuildConfig
import com.roobo.appreport.networklibrary.base.INetworkRequiredInfo
import getAppVersionCode
import getAppVersionName

/**
 * 接口请求头统一参数
 */
class NetworkRequestInfo(private val application: Application) : INetworkRequiredInfo {

    override fun getAppVersionName(): String {
        return application.getAppVersionName()
    }

    override fun getAppVersionCode(): String {
        return application.getAppVersionCode().toString()
    }

    override fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun getApplicationContext(): Application {
        return application
    }

    override fun getToken(): String {
        return getAuthorization()
    }

    private fun getAuthorization(): String {
       return ""
    }
}