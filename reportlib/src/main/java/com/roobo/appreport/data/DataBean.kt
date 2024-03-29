package com.roobo.appreport.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Poko
@Parcelize
data class Result(
    val appId: String,
    val clientId: String,
    val expiredTime: Int,
    val name: String,
    val timestamp: Int,
    val token: String
) : Parcelable


enum class TopCharEnum {
    Duration,
    KnowledgePoints,
    WatchVideo,
    AnswerQuestion
}

enum class KnowledgeCharEnum {
    All,
    Mastered,
    WeakSpot,
    Undetected
}

@Poko
@Parcelize
data class TopData(
    val bookMasterNum: Int,
    val bookNotEvaluatedNum: Int,
    val bookWeaknum: Int,
    val chapterList: List<Chapter>,
    val totalBookKnowledge: Int
) : Parcelable

@Poko
@Parcelize
data class Chapter(
    val chapterMasterNum: Int,
    val chapterName: String,
    val chapterNotEvaluatedNum: Int,
    val chapterWeakNum: Int,
    val knowledgeList: List<Knowledge>,
    val totalChapterKnowledge: Int
) : Parcelable

@Poko
@Parcelize
data class Knowledge(
    val classicState: Boolean,
    val knowledgeId: Int,
    val knowledgename: String,
    val noRecord: Boolean,
    val accuracy: Int?
) : Parcelable


@Poko
@Parcelize
data class DetailData(
    val monthDetail: List<MonthDetail>,
    val monthTime: Int,
    val weekDetail: List<WeekDetail>,
    val weekTime: Int,
    val weekUsedTime: Int
) : Parcelable

@Poko
@Parcelize
data class MonthDetail(
    val date: String,
    val knowledgeNum: Int,
    val questionNum: Int,
    val usedTime: Int,
    val videoNum: Int
) : Parcelable

@Poko
@Parcelize
data class WeekDetail(
    val date: String,
    val knowledgeNum: Int,
    val questionNum: Int,
    val usedTime: Int,
    val videoNum: Int
) : Parcelable

@Poko
@Parcelize
data class SubjectInfo(
    val subjectid: Int,
    val subjectname: String,
    val pinyin: String?,
    val opentype: Int
) : Parcelable

class LastSelectEntity {
    var uid = 0

    /*科目*/
    var subjectId: String? = null

    /*科目*/
    var subjectName: String? = null

    /*年级*/
    var gradeId: String? = null

    /*年级*/
    var gradeName: String? = null

    /*出版社*/
    var editionId: String? = null

    /*出版社*/
    var editionName: String? = null

    /*章节*/
    var chapterId: String? = null

    /*章节*/
    var chapterName: String? = null
}