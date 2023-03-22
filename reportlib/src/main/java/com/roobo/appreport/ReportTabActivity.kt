package com.roobo.appreport

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.roobo.appreport.adapter.TabFragAdapter
import com.roobo.appreport.data.DetailData
import com.roobo.appreport.databinding.ActivityReportTabBinding
import com.roobo.appreport.networklibrary.base.BaseResponse
import com.roobo.appreport.networklibrary.sEditionId
import com.roobo.appreport.networklibrary.sGradeId
import com.roobo.appreport.networklibrary.sSubjectId
import com.roobo.appreport.networklibrary.sToken
import com.roobo.appreport.repository.MainRepository
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ReportTabActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityReportTabBinding

    private val fragments = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_tab)

        sSubjectId = intent.getIntExtra("subjectId",-1)
        sGradeId = intent.getIntExtra("gradeId",-1)
        sEditionId = intent.getIntExtra("editionId",-1)
//        sDeviceId = intent.getStringExtra("deviceId") ?: ""
        sToken = intent.getStringExtra("token") ?: ""

        initBar()
        mBinding.apply {
            ivBack.setOnClickListener {
                finish()
            }

        }
        getDataRemote()
    }

    private val mMainRepository = MainRepository()

    private fun getDataRemote() {
        mMainRepository.jxwLearnCaseStats().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponse<DetailData>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(it: BaseResponse<DetailData>) {


                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Log.e("ddd", "${e.message}")
                }

                override fun onComplete() {
                }

            })


        initTabAndDatas(mutableListOf("语文", "数学", "英语", "历史", "地理", "生物", "化学"))
    }

    private fun initTabAndDatas(list: MutableList<String>) {
        list.forEach {
            val frag = CourseFragment.newInstance(it)
            fragments.add(frag)
        }

        val mAdapter = TabFragAdapter(this, fragments)
        mBinding.viewPager.adapter = mAdapter
        TabLayoutMediator(
            mBinding.tabLayout, mBinding.viewPager, true, true
        ) { tab, position ->
            Log.e("dlj", "$tab $position ")
            tab.text = list[position]
        }.attach()
    }


    private fun initBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .statusBarDarkFont(true)
            .init()
    }


}