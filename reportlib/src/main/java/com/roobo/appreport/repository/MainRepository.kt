package com.roobo.appreport.repository

import com.roobo.appreport.api.ApiService
import com.roobo.appreport.api.CommonNetApi
import com.roobo.appreport.data.DetailData
import com.roobo.appreport.data.TopData
import com.roobo.appreport.networklibrary.base.BaseResponse
import com.roobo.appreport.networklibrary.baseUrl
import io.reactivex.Observable

class MainRepository {


    fun jxwLearnCaseStats(
        deviceId: String = "8027088",
        token: String
    ): Observable<BaseResponse<DetailData>> {
        return CommonNetApi.sInstance.getService(ApiService::class.java, baseUrl)
            .jxwLearnCaseStats(deviceId, token)
    }


    fun jxwKnowledgeList(
        subjectId: Int,
        gradeId: Int,
        editionId: Int,
        deviceId: String,
        token: String
    ): Observable<BaseResponse<TopData>> {
        return CommonNetApi.sInstance.getService(ApiService::class.java, baseUrl)
            .jxwKnowledgeList(subjectId, gradeId, editionId, deviceId, token)
    }
}