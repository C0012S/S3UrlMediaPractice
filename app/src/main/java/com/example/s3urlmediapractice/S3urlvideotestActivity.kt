package com.example.s3urlmediapractice

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Button
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.android.synthetic.main.activity_s3urlvideotest.*

class S3urlvideotestActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s3urlvideotest)

        var video_start = findViewById<Button>(R.id.video_start)
        var video_end = findViewById<Button>(R.id.video_end)

        mediaPlayer = MediaPlayer()
        surfaceHolder = video_test_surfaceView.holder
        surfaceHolder.addCallback(this)

        video_start.setOnClickListener {
            Log.d("Video Start Button : ", "동영상 재생")

            mediaPlayer.start()
        }

        video_end.setOnClickListener {
            Log.d("Video End Button : ", "동영상 정지 & 메인 페이지로 이동")

            mediaPlayer.release() // mediaPlayer.release() : 초기화  // mediaPlayer.pause() : 일시 정지  // mediaPlayer.stop() : 정지

//            var intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent) // 인텐트로 페이지 이동 시 페이지 이동 기록이 남아서 뒤로 가기 버튼을 누르면 이 페이지로 다시 돌아온다. → 뒤로 가기 버튼으로 돌아온 후 '시작' 버튼을 누르면 Exception 발생
            finish() // 액티비티 종료  // 페이지 이동 기록 남지 않는다.
        }
    }

    override fun surfaceCreated(holder : SurfaceHolder) {
        val awsCredentials : AWSCredentials = BasicAWSCredentials("access_Key", "secret_Key") // IAM User의 (accessKey, secretKey)
        val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

        var bucketUrl = s3Client.getResourceUrl("bucket_Name", null).toString()
        var videoName : String? = "video_Name.mp4"

        try {
            mediaPlayer.setDataSource(bucketUrl + videoName)
            mediaPlayer.setDisplay(holder)
            mediaPlayer.prepare()

            Log.d("MediaPlayer : ", bucketUrl + videoName + " 재생")
        }
        catch (e: Exception) {
            Log.e("Exception : ", e.toString())
        }
    }

    override fun surfaceChanged(holder : SurfaceHolder, format : Int, width : Int, height : Int) {

    }

    override fun surfaceDestroyed(holder : SurfaceHolder) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}