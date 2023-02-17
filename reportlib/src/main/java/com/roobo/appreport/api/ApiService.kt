package com.roobo.appreport.api

import com.roobo.appreport.data.DetailData
import com.roobo.appreport.data.TopData
import com.roobo.appreport.networklibrary.base.BaseResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("jxwlearncase/stats")
    fun jxwLearnCaseStats(
        @Query("deviceId") deviceId: String = "8027088",
        @Query("token") token: String
    ): Observable<BaseResponse<DetailData>>

    @GET("jxwtiku/knowledge/list3")
    fun jxwKnowledgeList(
        @Query("subjectId") subjectId: Int,
        @Query("gradeId") gradeId: Int,
        @Query("editionId") editionId: Int,
        @Query("deviceId") deviceId: String,
        @Query("token") token: String
    ): Observable<BaseResponse<TopData>>

}