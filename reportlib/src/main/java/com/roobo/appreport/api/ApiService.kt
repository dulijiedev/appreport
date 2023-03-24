package com.roobo.appreport.api

import com.roobo.appreport.data.DetailData
import com.roobo.appreport.data.SubjectInfo
import com.roobo.appreport.data.TopData
import com.roobo.appreport.networklibrary.base.BaseResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {


    @GET("jxwlearncase/stats2")
    fun jxwLearnCaseStats(
//        @Query("deviceId") deviceId: String = "8027088",
//        @Query("token") token: String
    ): Observable<BaseResponse<DetailData>>

    @GET("jxwtiku/knowledge/list4")
    fun jxwKnowledgeList(
        @Query("subjectId") subjectId: Int,
        @Query("gradeId") gradeId: Int,
        @Query("editionId") editionId: Int,
//        @Query("deviceId") deviceId: String,
//        @Query("token") token: String
    ): Observable<BaseResponse<TopData>>

    @GET("jxwtiku/subject/list")
    fun jxwSubjectList(
        @Query("openType") subjectId: Int
    ): Observable<BaseResponse<List<SubjectInfo>>>

}