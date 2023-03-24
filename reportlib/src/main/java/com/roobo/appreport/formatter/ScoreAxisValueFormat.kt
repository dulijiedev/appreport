package com.roobo.appreport.formatter

import android.util.Log
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class ScoreAxisValueFormat(val hasScore:Boolean) :ValueFormatter() {

    private var mFormat = DecimalFormat("###,###,###,##0.0")

    override fun getFormattedValue(value: Float): String {
        Log.e("dlj======>"," $hasScore $value")
        if(hasScore) {
            return "${mFormat.format(value / 60)} 分"
        }else{
            return "${mFormat.format(value)}"
        }
    }
}