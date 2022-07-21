package com.example.s3urlmediapractice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var page_button = findViewById<Button>(R.id.page_button)
        var video_button = findViewById<Button>(R.id.video_button)

        page_button.setOnClickListener {
            Log.d("Button : ", "페이지 이동")

            var intent = Intent(applicationContext, S3urlmediaActivity::class.java)
            startActivity(intent)
        }

        video_button.setOnClickListener {
            Log.d("Button : ", "Video Test 페이지로 이동")

            var intent = Intent(applicationContext, S3urlvideotestActivity::class.java)
            startActivity(intent)
        }
    }

/*
    // 뒤로 가기 버튼 막기  // 메인 페이지에서 뒤로 가기 버튼을 막으면 뒤로 가기 버튼으로 어플 종료가 불가능하다.
    override fun onBackPressed() {
        //super.onBackPressed();
    }
*/
}