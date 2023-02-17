package com.roobo.appreport.formatter

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class ScoreAxisValueFormat :ValueFormatter() {

    private var mFormat = DecimalFormat("###,###,###,##0.0")

    override fun getFormattedValue(value: Float): String {
        return "${mFormat.format(value)} 分"
    }
}