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
        findViewById<Button>(R.id.btn_jump).setOnClickListener {
            val intent = Intent(this,ReportMainActivity::class.java)
            intent.putExtra("lessonName","lessonName")

            intent.putExtra("subjectId",2)
            intent.putExtra("gradeId",151)
            intent.putExtra("editionId",59)
            intent.putExtra("deviceId","8027088")
            intent.putExtra("token","login_user1298584dzogsesr4df1yg8cr1xc7wrv8j2sv1nz")

            startActivity(intent)
        }
        ReportSDK.getInstance().init(application,"http://api4.jiumentongbu.com/api/")
    }
}