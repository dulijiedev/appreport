package com.roobo.appreport

import android.database.Cursor
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.roobo.appreport.adapter.HolderAdapter
import com.roobo.appreport.adapter.TipAdapter
import com.roobo.appreport.data.*
import com.roobo.appreport.databinding.ActivityMainReportBinding
import com.roobo.appreport.formatter.*
import com.roobo.appreport.networklibrary.base.BaseResponse
import com.roobo.appreport.networklibrary.sToken
import com.roobo.appreport.repository.MainRepository
import com.roobo.appreport.utils.UIUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ReportMainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainReportBinding

    private var visible7 = true

    private var currentTopSize = 7
    private var mKnowledgeCharEnum: KnowledgeCharEnum = KnowledgeCharEnum.All
    private var topData: TopData? = null
    private var detailData: DetailData? = null

    private var mv: XYMarkerView? = null
    private val lessonList = mutableListOf<LastSelectEntity>()

    private var currentSelectEntity: LastSelectEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_report)
        sToken = intent.getStringExtra("token") ?: ""

        initBar()
        initHolder()
        mBinding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            initSevenChart()
            initStudyChart(true, chart, leftCount = 3)
            initLineChart(true)
            initKnowledge()
            initTipKnowledge()
            ivSwitch.setOnClickListener {
//                showSwitchDialog()
                showSwitchDialog(lessonList)
            }

            chart.isVisible = visible7
            lineChart.isVisible = !visible7
            llChangeData.setOnClickListener {
                visible7 = !visible7
                chart.isVisible = visible7
                lineChart.isVisible = !visible7

            }

            llTime.setBackgroundResource(R.drawable.border_selected_bkg)
            llAll.setBackgroundResource(R.drawable.border_selected_bkg)

            llTime.setOnClickListener {
                switchTopChartBkgAndData(TopCharEnum.Duration)
                initLineChart(true)
                initStudyChart(true, chart, leftCount = 3)
                setData(chart, TopCharEnum.Duration, detailData, 7)
            }
            llStudyCount.setOnClickListener {
                switchTopChartBkgAndData(TopCharEnum.KnowledgePoints)
                initLineChart(false)
                initStudyChart(false, chart, leftCount = 3)
                setData(chart, TopCharEnum.KnowledgePoints, detailData, 7)
            }
            llVideo.setOnClickListener {
                switchTopChartBkgAndData(TopCharEnum.WatchVideo)
                initLineChart(false)
                initStudyChart(false, chart, leftCount = 3)
                setData(chart, TopCharEnum.WatchVideo, detailData, 7)
            }
            llAnswer.setOnClickListener {
                switchTopChartBkgAndData(TopCharEnum.AnswerQuestion)
                initLineChart(false)
                initStudyChart(false, chart, leftCount = 3)
                setData(chart, TopCharEnum.AnswerQuestion, detailData, 7)
            }

            llAll.setOnClickListener {
                switchKnowledgeChartBar(KnowledgeCharEnum.All)
            }
            llMaster.setOnClickListener {
                switchKnowledgeChartBar(KnowledgeCharEnum.Mastered)
            }
            llWeak.setOnClickListener {
                switchKnowledgeChartBar(KnowledgeCharEnum.WeakSpot)
            }
            llUndetected.setOnClickListener {
                switchKnowledgeChartBar(KnowledgeCharEnum.Undetected)
            }

            llChangeData.setOnClickListener {
                val dialog = BottomSheetDialog(this@ReportMainActivity)
                val view = LayoutInflater.from(this@ReportMainActivity)
                    .inflate(R.layout.choose_data_layout, null, false)
                dialog.setContentView(view)
                val radioBtm7 = view.findViewById<RadioButton>(R.id.tv_7)
                val radioBtm14 = view.findViewById<RadioButton>(R.id.tv_14)
                val radioBtm30 = view.findViewById<RadioButton>(R.id.tv_30)
                if (currentTopSize == 7) {
                    radioBtm7.isChecked = true
                } else if (currentTopSize == 14) {
                    radioBtm14.isChecked = true
                } else {
                    radioBtm30.isChecked = true
                }
                view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                    dialog.dismiss()
                }
                view.findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
                    initLineChart(true)
                    if (radioBtm7.isChecked) {
                        lineChart.isVisible = false
                        chart.isVisible = true
                        currentTopSize = 7
                        setData(chart, TopCharEnum.Duration, detailData, 7)
                        llTime.performClick()
                    } else if (radioBtm14.isChecked) {
                        currentTopSize = 14
                        lineChart.isVisible = true
                        chart.isVisible = false
                        setLineCharData(TopCharEnum.Duration, detailData, 14)
                        llTime.performClick()
                    } else if (radioBtm30.isChecked) {
                        currentTopSize = 30
                        lineChart.isVisible = true
                        chart.isVisible = false
                        setLineCharData(TopCharEnum.Duration, detailData, 30)
                        llTime.performClick()
                    }
                    tvDate.text = "${currentTopSize}天内"
                    dialog.dismiss()
                }
                dialog.show()
            }
        }

        getDataRemote()
        getSelectLesson()
        getAllContent()
    }

    private fun initSevenChart() {
        mBinding.apply {
//            chart.description.isEnabled = false
//            chart.legend.isEnabled = false
//            chart.axisRight.setDrawGridLines(false)
//            chart.axisLeft.setDrawGridLines(false)
//            chart.axisRight.isEnabled = false
//            chart.setDrawBarShadow(true)
//            // add a nice and smooth animation
//            chart.animateY(1000)
            //
//            chart.description.isEnabled = false
//            chart.legend.isEnabled = false
//            chart.axisRight.setDrawGridLines(false)
//            chart.axisLeft.setDrawGridLines(false)
//            chart.axisRight.isEnabled = false
//            val xLabels = chart.xAxis
//            xLabels.position = XAxis.XAxisPosition.BOTTOM
//
//            // add a nice and smooth animation
//            chart.animateY(1500)
//
//            chart.setOnChartValueSelectedListener(this@MainActivity)
//            chart.setDrawBarShadow(true)
//            chart.setDrawValueAboveBar(true)
//            chart.setScaleEnabled(false)
//            chart.description.isEnabled = false
//            chart.setMaxVisibleValueCount(60)
//            chart.setPinchZoom(false)
//            chart.setDrawGridBackground(false)
//            chart.setGridBackgroundColor(UIUtils.getColor(R.color.column_color))
        }
    }


    /**
     * 知识点内容
     */
    private fun initKnowledge() {
        mBinding.apply {

            chartKnowledge.description.isEnabled = false
            chartKnowledge.legend.isEnabled = false
            chartKnowledge.axisRight.setDrawGridLines(false)
            chartKnowledge.axisLeft.setDrawGridLines(false)
            chartKnowledge.axisRight.isEnabled = false
            val xLabels = chartKnowledge.xAxis
            xLabels.position = XAxis.XAxisPosition.BOTTOM

            // add a nice and smooth animation
            chartKnowledge.animateY(1500)

            chartKnowledge.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                private val onValueSelectedRectF = RectF()

                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e == null) return

                    val bounds = onValueSelectedRectF
                    chartKnowledge.getBarBounds(e as BarEntry?, bounds)
                    val position: MPPointF = chart.getPosition(e, AxisDependency.LEFT)

                    Log.i("bounds", bounds.toString())
                    Log.i("position", position.toString())

                    Log.i(
                        "x-index",
                        "low: " + chart.getLowestVisibleX() + ", high: "
                                + chart.getHighestVisibleX()
                    )

                    MPPointF.recycleInstance(position)
                }

                override fun onNothingSelected() {

                }

            })
            chartKnowledge.setDrawBarShadow(true)
            chartKnowledge.setDrawValueAboveBar(true)
            chartKnowledge.setScaleEnabled(false)
            chartKnowledge.description.isEnabled = false
            chartKnowledge.setMaxVisibleValueCount(60)
            chartKnowledge.setPinchZoom(false)
            chartKnowledge.setDrawGridBackground(false)
            chartKnowledge.setGridBackgroundColor(UIUtils.getColor(R.color.column_color))
//            initKnowledgeChart(chartKnowledge, leftCount = 5)
            //多组数据内容
//            initKnowledgeData(chartKnowledge, 5)

        }
    }

    /**
     * 知识点 柱状图及切换
     */
    private fun switchKnowledgeChartBar(knowledgeType: KnowledgeCharEnum) {
        mv?.setCharEnum(knowledgeType)
        mHolderAdapter.changeChoose(knowledgeType)
        mBinding.apply {
            when (knowledgeType) {
                KnowledgeCharEnum.All -> {
                    //全部
                    llAll.setBackgroundResource(R.drawable.border_selected_bkg)
                    llMaster.setBackgroundResource(R.drawable.border_action_bkg)
                    llWeak.setBackgroundResource(R.drawable.border_action_bkg)
                    llUndetected.setBackgroundResource(R.drawable.border_action_bkg)
                }
                KnowledgeCharEnum.Mastered -> {
                    //已掌握
                    llAll.setBackgroundResource(R.drawable.border_action_bkg)
                    llMaster.setBackgroundResource(R.drawable.border_selected_bkg)
                    llWeak.setBackgroundResource(R.drawable.border_action_bkg)
                    llUndetected.setBackgroundResource(R.drawable.border_action_bkg)
                }
                KnowledgeCharEnum.WeakSpot -> {
                    //薄弱点
                    llAll.setBackgroundResource(R.drawable.border_action_bkg)
                    llMaster.setBackgroundResource(R.drawable.border_action_bkg)
                    llWeak.setBackgroundResource(R.drawable.border_selected_bkg)
                    llUndetected.setBackgroundResource(R.drawable.border_action_bkg)
                }
                KnowledgeCharEnum.Undetected -> {
                    //未检测
                    llAll.setBackgroundResource(R.drawable.border_action_bkg)
                    llMaster.setBackgroundResource(R.drawable.border_action_bkg)
                    llWeak.setBackgroundResource(R.drawable.border_action_bkg)
                    llUndetected.setBackgroundResource(R.drawable.border_selected_bkg)
                }
            }

            topData?.let { initKnowledgeData(chartKnowledge, it, knowledgeType = knowledgeType) }
                ?: kotlin.run {
                    chartKnowledge.clear()
                    mBinding.tvLessonName.text = "--"
                }
        }

    }

    /**
     * 今日学习情况 图表
     */
    private fun switchTopChartBkgAndData(type: TopCharEnum) {
        mBinding.apply {
            when (type) {
                TopCharEnum.Duration -> {
                    llTime.setBackgroundResource(R.drawable.border_selected_bkg)
                    llStudyCount.setBackgroundResource(R.drawable.border_action_bkg)
                    llVideo.setBackgroundResource(R.drawable.border_action_bkg)
                    llAnswer.setBackgroundResource(R.drawable.border_action_bkg)
                }
                TopCharEnum.KnowledgePoints -> {
                    llTime.setBackgroundResource(R.drawable.border_action_bkg)
                    llStudyCount.setBackgroundResource(R.drawable.border_selected_bkg)
                    llVideo.setBackgroundResource(R.drawable.border_action_bkg)
                    llAnswer.setBackgroundResource(R.drawable.border_action_bkg)
                }
                TopCharEnum.WatchVideo -> {
                    llTime.setBackgroundResource(R.drawable.border_action_bkg)
                    llStudyCount.setBackgroundResource(R.drawable.border_action_bkg)
                    llVideo.setBackgroundResource(R.drawable.border_selected_bkg)
                    llAnswer.setBackgroundResource(R.drawable.border_action_bkg)
                }
                TopCharEnum.AnswerQuestion -> {
                    llTime.setBackgroundResource(R.drawable.border_action_bkg)
                    llStudyCount.setBackgroundResource(R.drawable.border_action_bkg)
                    llVideo.setBackgroundResource(R.drawable.border_action_bkg)
                    llAnswer.setBackgroundResource(R.drawable.border_selected_bkg)
                }
            }
        }
        if (detailData != null) {
            if (currentTopSize == 7) {
                setData(mBinding.chart, type, detailData, 7)
            } else {
                setLineCharData(type, detailData, currentTopSize)
            }
        }
    }

    private fun initLineChart(hasScore: Boolean = true) {
        mBinding.apply {

            // background color
            lineChart.setBackgroundColor(Color.WHITE)
            // disable description text
            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            // enable touch gestures
            lineChart.setTouchEnabled(true)
            // set listeners
//            lineChart.setOnChartValueSelectedListener()
            lineChart.setDrawGridBackground(false)
            lineChart.axisRight.isEnabled = false


            val xAxisFormatter = TimeAxisValueFormatter(currentTopSize - 1)

            val xAxis: XAxis = lineChart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // only intervals of 1 day
            xAxis.labelCount = 5 //TODO 显示14  30 天
            xAxis.valueFormatter = xAxisFormatter
//            xAxis.axisLineColor = UIUtils.getColor(R.color.bottom_line)
            xAxis.enableGridDashedLine(10f, 10f, 0f)


            val custom = ScoreAxisValueFormat(hasScore)

            val leftAxis: YAxis = lineChart.axisLeft
            leftAxis.setLabelCount(6, false)
            leftAxis.valueFormatter = custom
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f
            leftAxis.axisMinimum = 0f

            // horizontal grid lines
            leftAxis.enableGridDashedLine(10f, 10f, 0f)

            leftAxis.axisMinimum = 0f

            leftAxis.setDrawLimitLinesBehindData(false)
            xAxis.setDrawLimitLinesBehindData(false)

            lineChart.animateX(1500)

            // get the legend (only possible after setting data)
            val l = lineChart.legend
            l.setDrawInside(false)

            // draw legend entries as lines
            l.form = Legend.LegendForm.LINE
        }
    }

    /**
     * 初始化学习情况图表
     */
    private fun initStudyChart(hasScore: Boolean, chart: BarChart, leftCount: Int = 2) {

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            private val onValueSelectedRectF = RectF()

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e == null) return

                val bounds = onValueSelectedRectF
                chart.getBarBounds(e as BarEntry?, bounds)
                val position: MPPointF = chart.getPosition(e, AxisDependency.LEFT)

                Log.i("bounds", bounds.toString())
                Log.i("position", position.toString())

                Log.i(
                    "x-index",
                    "low: " + chart.getLowestVisibleX() + ", high: "
                            + chart.getHighestVisibleX()
                )

                MPPointF.recycleInstance(position)
            }

            override fun onNothingSelected() {

            }

        })
        chart.setDrawBarShadow(true)
        chart.setDrawValueAboveBar(true)
        chart.setScaleEnabled(true)
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(60)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.legend.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.setGridBackgroundColor(UIUtils.getColor(R.color.column_color))


        val xAxisFormatter = TimeAxisValueFormatter()

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.labelCount = 7
        xAxis.valueFormatter = xAxisFormatter
        xAxis.axisLineColor = UIUtils.getColor(R.color.bottom_line)

        val custom = ScoreAxisValueFormat(hasScore)

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.setLabelCount(leftCount, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMinimum = 0f


        val rightAxis: YAxis = chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setLabelCount(8, false)
        rightAxis.setValueFormatter(custom)
        rightAxis.spaceTop = 15f
        rightAxis.axisMinimum = 0f


        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f

    }


    /**
     * 初始化学习情况图表
     */
    private fun initKnowledgeChart(chart: BarChart, leftCount: Int = 2) {

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {

            private val onValueSelectedRectF = RectF()

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e == null) return

                val bounds: RectF = onValueSelectedRectF
                chart.getBarBounds(e as BarEntry?, bounds)
                val position = chart.getPosition(e, AxisDependency.LEFT)

                Log.i("bounds", bounds.toString())
                Log.i("position", position.toString())

                Log.i(
                    "x-index",
                    "low: " + chart.lowestVisibleX + ", high: "
                            + chart.highestVisibleX + ", e.index:" + e.x
                )

                MPPointF.recycleInstance(position)
                val index = e.x.toInt()
                mHolderAdapter.setDatas(topData?.chapterList?.get(index))
                mBinding.tvLessonName.text = topData?.chapterList?.get(index)?.chapterName ?: ""
            }

            override fun onNothingSelected() {

            }

        })
        chart.setDrawBarShadow(true)
        chart.setDrawValueAboveBar(true)
        chart.setScaleEnabled(false)
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(60)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.setGridBackgroundColor(UIUtils.getColor(R.color.column_color))


        val xAxisFormatter = PointAxisValueFormatter()

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.labelCount = 5
        xAxis.valueFormatter = xAxisFormatter
        xAxis.setDrawLabels(false)
        xAxis.axisLineColor = UIUtils.getColor(R.color.bottom_line)

        val custom = PointAxisValueFormat()

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.setLabelCount(leftCount, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMinimum = 0f


        val rightAxis: YAxis = chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setLabelCount(8, false)
        rightAxis.setValueFormatter(custom)
        rightAxis.spaceTop = 15f
        rightAxis.axisMinimum = 0f


        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f


        chart.setExtraOffsets(0f, 0f, 0f, 0f)


        mv = XYMarkerView(this, xAxisFormatter)
        mv?.chartView = chart // For bounds control
        chart.marker = mv // Set the marker to the chart
    }


    private fun setData(chart: BarChart, type: TopCharEnum, data: DetailData?, count: Int) {
        val values = mutableListOf<BarEntry>()
        val weekData = data?.weekDetail
        val monthData = data?.monthDetail

        val timeList = mutableListOf<Int>()
        val knowledgeList = mutableListOf<Int>()
        val questionList = mutableListOf<Int>()
        val videoList = mutableListOf<Int>()

        if (count == 7) {
            weekData?.forEach {
                timeList.add(it.usedTime / 60)
                knowledgeList.add(it.knowledgeNum)
                questionList.add(it.questionNum)
                videoList.add(it.videoNum)
            }
        } else if (count == 14) {
            monthData?.takeLast(14)?.forEach {
                timeList.add(it.usedTime / 60)
                knowledgeList.add(it.knowledgeNum)
                questionList.add(it.questionNum)
                videoList.add(it.videoNum)
            }
        } else if (count == 30) {
            monthData?.forEach {
                timeList.add(it.usedTime / 60)
                knowledgeList.add(it.knowledgeNum)
                questionList.add(it.questionNum)
                videoList.add(it.videoNum)
            }
        }

        when (type) {
            TopCharEnum.Duration -> {
                for (i in timeList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            timeList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
            TopCharEnum.KnowledgePoints -> {

                for (i in knowledgeList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            knowledgeList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
            TopCharEnum.WatchVideo -> {
                for (i in videoList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            videoList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
            TopCharEnum.AnswerQuestion -> {
                for (i in questionList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            questionList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
        }

        var set1: BarDataSet? = null

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "当日学习情况")
            set1.setDrawIcons(false)
            set1.setDrawValues(false)
            set1.barShadowColor = UIUtils.getColor(R.color.column_color)

            val startColor = ContextCompat.getColor(this, android.R.color.holo_blue_light)
            set1.color = startColor

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.9f
            chart.data = data
        }
        chart.setDrawBarShadow(true)
        chart.invalidate()
        chart.animateY(1000)
        chart.notifyDataSetChanged()
    }


    private fun initKnowledgeData(
        chart: BarChart,
        topData: TopData,
        knowledgeType: KnowledgeCharEnum = KnowledgeCharEnum.All
    ) {
        val values = mutableListOf<BarEntry>()
        chart.clear()
        for (i in topData.chapterList.indices) {
            val val1 = topData.chapterList[i].chapterMasterNum.toFloat()
            val val2 = topData.chapterList[i].chapterWeakNum.toFloat()
            val val3 = topData.chapterList[i].chapterNotEvaluatedNum.toFloat()


            when (knowledgeType) {
                KnowledgeCharEnum.All -> {
                    values.add(
                        BarEntry(
                            i.toFloat(), floatArrayOf(val1, val2, val3),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
                KnowledgeCharEnum.Mastered -> {
                    values.add(
                        BarEntry(
                            i.toFloat(), floatArrayOf(val1),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
                KnowledgeCharEnum.WeakSpot -> {
                    values.add(
                        BarEntry(
                            i.toFloat(), floatArrayOf(val2),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
                KnowledgeCharEnum.Undetected -> {
                    values.add(
                        BarEntry(
                            i.toFloat(), floatArrayOf(val3),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
        }


        var set1: BarDataSet? = null

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {

            set1 = BarDataSet(values, "Statistics Vienna 2014")
            set1.setDrawIcons(false)
            when (knowledgeType) {
                KnowledgeCharEnum.All -> {
                    set1.colors = listOf(
                        UIUtils.getColor(R.color.knowledget_one),
                        UIUtils.getColor(R.color.knowledget_two),
                        UIUtils.getColor(R.color.knowledget_three)
                    )
                }
                KnowledgeCharEnum.Mastered -> {
                    set1.colors = listOf(UIUtils.getColor(R.color.knowledget_one))
                }
                KnowledgeCharEnum.WeakSpot -> {
                    set1.colors = listOf(UIUtils.getColor(R.color.knowledget_two))
                }
                KnowledgeCharEnum.Undetected -> {
                    set1.colors = listOf(UIUtils.getColor(R.color.knowledget_three))
                }
            }

            set1.stackLabels = arrayOf("Births", "Divorces", "Marriages")
            val dataSets = java.util.ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}"
                }
            })
            data.setValueTextColor(Color.WHITE)
            chart.data = data
        }
        chart.setFitBars(true)
        chart.invalidate()
        chart.notifyDataSetChanged()
    }


    private fun setLineCharData(type: TopCharEnum, data: DetailData?, count: Int) {
        val values = mutableListOf<Entry>()
        val weekData = data?.weekDetail
        val monthData = data?.monthDetail

        val timeList = mutableListOf<Int>()
        val knowledgeList = mutableListOf<Int>()
        val questionList = mutableListOf<Int>()
        val videoList = mutableListOf<Int>()

        when (count) {
            7 -> {
                weekData?.forEach {
                    timeList.add(it.usedTime / 60)
                    knowledgeList.add(it.knowledgeNum)
                    questionList.add(it.questionNum)
                    videoList.add(it.videoNum)
                }
            }
            14 -> {
                monthData?.takeLast(14)?.forEach {
                    timeList.add(it.usedTime / 60)
                    knowledgeList.add(it.knowledgeNum)
                    questionList.add(it.questionNum)
                    videoList.add(it.videoNum)
                }
            }
            30 -> {
                monthData?.forEach {
                    timeList.add(it.usedTime / 60)
                    knowledgeList.add(it.knowledgeNum)
                    questionList.add(it.questionNum)
                    videoList.add(it.videoNum)
                }
            }
        }

        when (type) {
            TopCharEnum.Duration -> {
                for (i in timeList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            timeList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
            TopCharEnum.KnowledgePoints -> {

                for (i in knowledgeList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            knowledgeList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
            TopCharEnum.WatchVideo -> {
                for (i in videoList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            videoList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
            TopCharEnum.AnswerQuestion -> {
                for (i in questionList.indices) {
                    values.add(
                        BarEntry(
                            i.toFloat(),
                            questionList[i].toFloat(),
                            ContextCompat.getDrawable(this, R.mipmap.star)
                        )
                    )
                }
            }
        }
        val set1: LineDataSet
        if (mBinding.lineChart.getData() != null &&
            mBinding.lineChart.getData().getDataSetCount() > 0
        ) {
            set1 = mBinding.lineChart.getData().getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            mBinding.lineChart.getData().notifyDataChanged()
            mBinding.lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider ->
                    mBinding.lineChart.getAxisLeft().getAxisMinimum()
                }
            //是否绘制填充
            set1.setDrawFilled(false)
            val dataSets = java.util.ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            mBinding.lineChart.setData(data)
        }
        mBinding.lineChart.invalidate()
        mBinding.lineChart.animateX(1000)
        mBinding.lineChart.notifyDataSetChanged()
    }


    private val mMainRepository = MainRepository()
    private fun getDataRemote() {
        mMainRepository.jxwLearnCaseStats(
//            deviceId = sDeviceId,
//            token = sToken
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponse<DetailData>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(it: BaseResponse<DetailData>) {
                    this@ReportMainActivity.detailData = it.data
                    setData(mBinding.chart, TopCharEnum.Duration, it.data, 7)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    print("ddd ${e.message}")
                    Log.e("ddd", "${e.message}")
                }

                override fun onComplete() {
                }

            })
    }

    private fun getKnowDataRemote(currentSelectEntity: LastSelectEntity) {
        mMainRepository.jxwKnowledgeList(
            subjectId = currentSelectEntity.subjectId?.toInt()?:0,
            gradeId = currentSelectEntity.gradeId?.toInt()?:0,
            editionId = currentSelectEntity.editionId?.toInt()?:0,
//            deviceId = sDeviceId,
//            token = sToken
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<BaseResponse<TopData>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(it: BaseResponse<TopData>) {
                    this@ReportMainActivity.topData = it.data

                    mBinding.tvTotalKnowledge.text = "${it.data?.totalBookKnowledge ?: "--"}"
                    mBinding.tvMasterNumber.text = "${it.data?.bookMasterNum ?: "--"}"
                    mBinding.tvWeakNumber.text = "${it.data?.bookWeaknum ?: "--"}"
                    mBinding.tvUnevaluatedNumber.text = "${it.data?.bookNotEvaluatedNum ?: "--"}"
                    initKnowledgeChart(
                        mBinding.chartKnowledge,
                        leftCount = 4
                    )
                    it.data?.apply {
                        //多组数据内容
                        initKnowledgeData(mBinding.chartKnowledge, this)
                    } ?: kotlin.run {
                        mBinding.chartKnowledge.clear()
                        switchKnowledgeChartBar(KnowledgeCharEnum.All)
                        mBinding.tvLessonName.text = "--"
                    }

                    val list = it.data?.chapterList?.map {
                        it.chapterName
                    }?.toList()
                    mv?.setTitles(list)


                    adapter.setData(list?.toList())

                    mHolderAdapter.setDatas(it.data?.chapterList?.firstOrNull())
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    print("ddd ${e.message}")
                    Log.e("ddd", "${e.message}")
                }

                override fun onComplete() {

                }

            })
    }

    private val adapter = TipAdapter()
    private fun initTipKnowledge() {
        mBinding.apply {
            listTip.layoutManager =
                LinearLayoutManager(this@ReportMainActivity, LinearLayoutManager.HORIZONTAL, false)
            listTip.adapter = adapter
        }
    }

    private fun initBar() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .statusBarDarkFont(true)
            .init()
    }

    private val mHolderAdapter = HolderAdapter()


    private fun initHolder() {
        mBinding.apply {
            listHolder.layoutManager = LinearLayoutManager(this@ReportMainActivity)
            listHolder.adapter = mHolderAdapter
            listHolder.setNestedScrollingEnabled(false);
            listHolder.setHasFixedSize(true)
        }
    }

    private var choseBook: Boolean = false

    private fun getSelectLesson() {
        Observable.create<LastSelectEntity> {
            val selectEntity = getContentLesson()
            if (selectEntity?.subjectId != null) {
                it.onNext(selectEntity)
            } else {
                it.onError(Throwable("empty"))
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<LastSelectEntity> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: LastSelectEntity) {
                    currentSelectEntity = t
                    choseBook = true
                    Log.e("dlj====", "${currentSelectEntity?.subjectId}")
                    if (currentSelectEntity?.subjectId?.isEmpty() == true) {
                        return
                    }
                    currentSelectEntity?.let { getKnowDataRemote(it) }
                    mBinding.tvTips.text ="${currentSelectEntity?.editionName?:""}${currentSelectEntity?.subjectName?:""}${currentSelectEntity?.gradeName?:""}"// currentSelectEntity?.subjectName ?: "--"
                }

                override fun onError(e: Throwable) {
                    choseBook = false
                    getAllContent()
                }

                override fun onComplete() {

                }

            })
    }

    private fun getAllContent() {
        Observable.create<MutableList<LastSelectEntity>> {
            val selectEntity = getAllLesson()
            if (selectEntity.isNotEmpty()) {
                it.onNext(selectEntity)
            } else {
                it.onError(Throwable("empty"))
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MutableList<LastSelectEntity>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MutableList<LastSelectEntity>) {
                    Log.e("dljjj", "onNext ${t.size}")
                    if (!choseBook) {
                        currentSelectEntity = t.first()
                        Log.e(
                            "dljjj",
                            "sss ${currentSelectEntity?.subjectName} ${currentSelectEntity?.subjectId}"
                        )
                        if (currentSelectEntity?.subjectId?.isEmpty() == true) {
                            return
                        }
                        choseBook = true
                        currentSelectEntity?.let { getKnowDataRemote(it) }
                        mBinding.tvTips.text = "${currentSelectEntity?.editionName?:""}${currentSelectEntity?.subjectName?:""}${currentSelectEntity?.gradeName?:""}"//currentSelectEntity?.subjectName ?: "--"

                        lessonList.clear()
                        lessonList.addAll(t)

                        currentSelectEntity?.let { getKnowDataRemote(it) }
                    }

                }

                override fun onError(e: Throwable) {
                    Log.e("dljjj", "onError ${e.message}")
                    choseBook = false
                }

                override fun onComplete() {

                }

            })
    }

    private fun getContentLesson(): LastSelectEntity? {
        val uri: Uri =
            Uri.parse("content://com.jxw.question.LastSelectProvider/query_last_select_by_subject")
        /*对应的科目*/
        val selection = ""
        val cursor: Cursor? = contentResolver.query(uri, null, selection, null, null)
        var entity: LastSelectEntity? = null
        if (cursor != null) {
            while (cursor.moveToNext()) {
                entity = LastSelectEntity()
                val subjectIdIndex: Int = cursor.getColumnIndex("subjectId")
                if (subjectIdIndex != -1) {
                    val subjectId: String = cursor.getString(subjectIdIndex)
                    Log.e("Test999", "日志输出1------subjectId:$subjectId")
                    if (subjectId.isBlank()) {
                        continue
                    }
                    entity.subjectId = subjectId
                } else {
                    choseBook = false
                    //没有选中的
//                    break
                }
                val subjectNameIndex: Int = cursor.getColumnIndex("subjectName")
                if (subjectNameIndex != -1) {
                    val subjectName: String = cursor.getString(subjectNameIndex)
                    Log.e("Test999", "日志输出2------subjectName:$subjectName")
                    entity.subjectName = subjectName
                }

                val gradeIdIndex: Int = cursor.getColumnIndex("gradeId")
                if (gradeIdIndex != -1) {
                    val gradeId: String = cursor.getString(gradeIdIndex)
                    Log.e("Test999", "日志输出3------gradeId:$gradeId")
                }
                val gradeNameIndex: Int = cursor.getColumnIndex("gradeName")
                if (gradeNameIndex != -1) {
                    val gradeName: String = cursor.getString(gradeNameIndex)
                    Log.e("Test999", "日志输出4------gradeName:$gradeName")
                }
                val editionIdIndex: Int = cursor.getColumnIndex("editionId")
                if (editionIdIndex != -1) {
                    val editionId: String = cursor.getString(editionIdIndex)
                    Log.e("Test999", "日志输出5------editionId:$editionId")
                }
                val editionNameIndex: Int = cursor.getColumnIndex("editionName")
                if (editionNameIndex != -1) {
                    val editionName: String = cursor.getString(editionNameIndex)
                    Log.e("Test999", "日志输出6------editionName:$editionName")
                }


                val chapterIdIndex: Int = cursor.getColumnIndex("chapterId")
                if (editionNameIndex != -1) {
                    val chapterId: String = cursor.getString(chapterIdIndex)
                    Log.e("Test999", "日志输出7------chapterId:$chapterId")
                }

                val chapterNameIndex: Int = cursor.getColumnIndex("chapterName")
                if (chapterNameIndex != -1) {
                    val chapterName: String = cursor.getString(chapterNameIndex)
                    Log.e("Test999", "日志输出8------chapterName:$chapterName")
                }

                if (entity.subjectId != null) {
                    break
                }
            }
        }
        return entity
    }

    private fun getAllLesson(): MutableList<LastSelectEntity> {
        val list = mutableListOf<LastSelectEntity>()
        val uri = Uri.parse("content://com.jxw.question.LastSelectProvider/query_last_select")
        val cursor: Cursor? =
            contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val entity = LastSelectEntity()
                val subjectIdIndex: Int = cursor.getColumnIndex("subjectId")
                if (subjectIdIndex != -1) {
                    val subjectId: String = cursor.getString(subjectIdIndex)
                    Log.e("Test999", "日志输出2------subjectId:$subjectId")
                    if (subjectId.isBlank()) {
                        continue
                    }
                    entity.subjectId = subjectId
                }
                val subjectNameIndex: Int = cursor.getColumnIndex("subjectName")
                if (subjectNameIndex != -1) {
                    val subjectName: String = cursor.getString(subjectNameIndex)
                    Log.e("Test999", "日志输出2------subjectName:$subjectName")
                    entity.subjectName = subjectName
                }

                val gradeIdIndex: Int = cursor.getColumnIndex("gradeId")
                if (gradeIdIndex != -1) {
                    val gradeId: String = cursor.getString(gradeIdIndex)
                    Log.e("Test999", "日志输出3------gradeId:$gradeId")
                    entity.gradeId = gradeId
                }
                val gradeNameIndex: Int = cursor.getColumnIndex("gradeName")
                if (gradeNameIndex != -1) {
                    val gradeName: String = cursor.getString(gradeNameIndex)
                    Log.e("Test999", "日志输出4------gradeName:$gradeName")
                    entity.gradeName = gradeName
                }
                val editionIdIndex: Int = cursor.getColumnIndex("editionId")
                if (editionIdIndex != -1) {
                    val editionId: String = cursor.getString(editionIdIndex)
                    Log.e("Test999", "日志输出5------editionId:$editionId")
                    entity.editionId = editionId
                }
                val editionNameIndex: Int = cursor.getColumnIndex("editionName")
                if (editionNameIndex != -1) {
                    val editionName: String = cursor.getString(editionNameIndex)
                    Log.e("Test999", "日志输出6------editionName:$editionName")
                    entity.editionName = editionName
                }


                val chapterIdIndex: Int = cursor.getColumnIndex("chapterId")
                if (chapterIdIndex != -1) {
                    val chapterId: String = cursor.getString(chapterIdIndex)
                    Log.e("Test999", "日志输出7------chapterId:$chapterId")
                    entity.chapterId = chapterId
                }

                val chapterNameIndex: Int = cursor.getColumnIndex("chapterName")
                if (chapterNameIndex != -1) {
                    val chapterName: String = cursor.getString(chapterNameIndex)
                    Log.e("Test999", "日志输出8------chapterName:$chapterName")
                    entity.chapterName = chapterName
                }
                list.add(entity)
            }
        }
//        for (i in 0..10) {
//            val entity1 = LastSelectEntity()
//            entity1.subjectName = "语文$i"
//            entity1.subjectId = "123123$i"
//            list.add(entity1)
//        }
        return list
    }

//    private var courseDialog: BottomSheetDialog? = null
//    private var switchAdapter: SubjectAdapter? = null
//    private fun initSwitchCourse(list: MutableList<LastSelectEntity>) {
//        val courseDialog = BottomSheetDialog(this@ReportMainActivity)
//        val switchBinding = DataBindingUtil.inflate<SwitchCourseLayoutBinding>(
//            layoutInflater,
//            R.layout.switch_course_layout,
//            null,
//            false
//        )
//        courseDialog?.setContentView(switchBinding.root)
//        switchBinding.listCourse.layoutManager = LinearLayoutManager(this)
//        switchAdapter = SubjectAdapter(list, onItemClick = { data, index ->
//            val old = switchAdapter?.selectIndex ?: 0
//            switchAdapter?.selectIndex = index
//            switchAdapter?.notifyItemChanged(old)
//            switchAdapter?.notifyItemChanged(index)
//        })
//        val selectIndex = switchAdapter?.list?.indexOf(currentSelectEntity) ?: 0
//        switchAdapter?.selectIndex = selectIndex
//
//        switchBinding.listCourse.adapter = switchAdapter
//        switchBinding.tvSave.setOnClickListener {
//            val index = switchAdapter?.selectIndex?:0
//            if(index>=0 && index<list.size){
//                currentSelectEntity = list[index]
//            }
//            currentSelectEntity?.subjectId?.toInt()?.let { it1 -> getKnowDataRemote(it1) }
//            mBinding.tvTips.text = currentSelectEntity?.subjectName ?: "--"
//            courseDialog?.dismiss()
//        }
//        courseDialog.show()
//    }

    private fun showSwitchDialog(list: MutableList<LastSelectEntity>) {
        if (list.isEmpty()) {
            Toast.makeText(this, "课程为空", Toast.LENGTH_SHORT).show()
            return
        }
        CourseDialog(this, list, currentSelectEntity) {
            if (it?.subjectId?.isEmpty() == true) {
                Toast.makeText(this, "课程暂未添加", Toast.LENGTH_SHORT).show()
                return@CourseDialog
            }
            currentSelectEntity = it
            currentSelectEntity?.let { it1 -> getKnowDataRemote(it1) }
            mBinding.tvTips.text = "${currentSelectEntity?.editionName?:""}${currentSelectEntity?.subjectName?:""}${currentSelectEntity?.gradeName?:""}"//currentSelectEntity?.subjectName ?: "--"
        }.show()
    }
}