package com.roobo.appreport.networklibrary.base

import android.app.Application

interface INetworkRequiredInfo {
    /**
     * @description： 获取AppVersionName
     */
    fun getAppVersionName(): String

    /**
     * @description： 获取版本号
     */
    fun getAppVersionCode(): String

    /**
     * @description： 是否是debug模式
     */
    fun isDebug(): Boolean

    /**
     * @description： 全局application
     */
    fun getApplicationContext(): Application

    /**
     * 获取token
     */
    fun getToken(): String
}