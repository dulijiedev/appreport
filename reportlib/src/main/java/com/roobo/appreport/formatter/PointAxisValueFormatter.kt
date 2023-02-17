package com.roobo.appreport.formatter

import com.github.mikephil.charting.formatter.ValueFormatter

class PointAxisValueFormatter : ValueFormatter() {


    override fun getFormattedValue(value: Float): String {
        val value = when (value) {
            0f -> {
                "1 准备课"
            }
            1f -> {
                "2 位置"
            }
            2f -> {
                "3 1~5\n的认识和加减法"
            }
            3f -> {
                "4 认识图\n形(一)"
            }
            4f -> {
                "5 6~1\n0的认识和\n加减法"
            }
            else -> {
                ""
            }
        }
        return value
    }
}