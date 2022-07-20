package com.example.s3urlmediapractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var page_button = findViewById<Button>(R.id.page_button)

        page_button.setOnClickListener {
            Log.d("Button : ", "페이지 이동")

            var intent = Intent(applicationContext, S3urlmediaActivity::class.java)
            startActivity(intent)
        }
    }
}