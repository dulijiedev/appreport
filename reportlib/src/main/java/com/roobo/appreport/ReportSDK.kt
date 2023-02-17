package com.roobo.appreport

import android.app.Application
import com.roobo.appreport.api.NetworkRequestInfo
import com.roobo.appreport.networklibrary.base.NetworkApi
import com.roobo.appreport.networklibrary.baseUrl
import com.roobo.appreport.utils.UIUtils


class ReportSDK {

    companion object {
        @JvmStatic
        private var instance: ReportSDK? = null

        @JvmStatic
        fun getInstance(): ReportSDK {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ReportSDK()
                    }
                }
            }
            return instance!!
        }
    }

    lateinit var application: Application

    fun init(application: Application,baseUrlRemote:String = "http://api4.jiumentongbu.com/api/") {
        this.application=application
        UIUtils.init(application)
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        }
        baseUrl=baseUrlRemote
        NetworkApi.initData(NetworkRequestInfo(application))
    }
}