package com.roobo.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.roobo.appreport.ReportMainActivity

import android.content.Intent
import com.roobo.appreport.ReportSDK

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        ReportSDK.getInstance().init(application,"http://api4.jiumentongbu.com/api/")
        findViewById<Button>(R.id.btn_jump).setOnClickListener {
            val intent = Intent(this,ReportMainActivity::class.java)
            intent.putExtra("lessonName","lessonName")
            intent.putExtra("subjectId",2)
            intent.putExtra("gradeId",151)
            intent.putExtra("editionId",59)
            intent.putExtra("token","login_jxwdevice7994112qw1hq1lr0vfmrk7sz47jbvhekxeawbvq")
            startActivity(intent)
        }

    }
}