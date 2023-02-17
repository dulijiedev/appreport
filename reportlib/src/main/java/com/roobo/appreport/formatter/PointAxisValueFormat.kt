package com.roobo.appreport.formatter

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class PointAxisValueFormat :ValueFormatter() {

    private var mFormat = DecimalFormat("###,###,###,##")

    override fun getFormattedValue(value: Float): String {
        return "${mFormat.format(value)} 个"
    }
}