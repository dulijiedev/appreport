package com.roobo.appreport

import android.app.Application
import com.roobo.appreport.api.NetworkRequestInfo
import com.roobo.appreport.networklibrary.base.NetworkApi
import com.roobo.appreport.utils.UIUtils
import kotlin.properties.Delegates

class MyApp :Application() {

    companion object {
        var sInstance: MyApp by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        UIUtils.init(this)

        NetworkApi.initData(NetworkRequestInfo(this))
    }
}