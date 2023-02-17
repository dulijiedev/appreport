package com.roobo.appreport.formatter

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TimeAxisValueFormatter(val maxValue:Int=6) : ValueFormatter() {


    override fun getFormattedValue(value: Float): String {
        val days=value.toInt()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        calendar.add(Calendar.DATE,days-maxValue)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DATE)
//        Log.e("dlj===","$year-$month-$day  value:${value}")
        val sdf = SimpleDateFormat("MM-dd")
        val dd = calendar.time
        val date = sdf.format(dd)
        return date
    }
}