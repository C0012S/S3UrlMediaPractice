package com.example.s3urlmediapractice

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.android.synthetic.main.activity_s3urlvideotest.*
import kotlin.properties.Delegates

class S3urlvideotestActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlayed by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s3urlvideotest)

        var video_start = findViewById<Button>(R.id.video_start)
        var video_end = findViewById<Button>(R.id.video_end)
        isPlayed = false

        mediaPlayer = MediaPlayer()
        surfaceHolder = video_test_surfaceView.holder
        surfaceHolder.addCallback(this)

        video_start.setOnClickListener {
            Log.d("Video Start Button : ", "동영상 재생")

            mediaPlayer.start()
            isPlayed = true
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

    override fun onUserLeaveHint() { // 홈 버튼 감지
        super.onUserLeaveHint()

        Log.d("Home Button : ", "이벤트 감지")

        if (isPlayed) {
            // 이벤트 작성
            mediaPlayer.release() // 홈 버튼 누른 후 다시 돌아오면 영상 종료된다. 그 후 다시 '시작' 버튼을 누르면 E/AndroidRuntime: FATAL EXCEPTION: main  Process: com.example.s3urlmediapractice, PID: 9545  java.lang.IllegalStateException: java.lang.IllegalStateException 발생

//            var intent = Intent(applicationContext, MainActivity::class.java)
//        startActivity(intent) // 인텐트로 페이지 이동 시 홈 버튼을 누른 후 이동할 페이지로 앱이 자동으로 다시 뜬다.

            val builder = AlertDialog.Builder(this)
            builder.setTitle("동영상 종료")
                .setMessage("동영상 재생이 종료됩니다.")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, id ->
//                    startActivity(intent) // 인텐트로 페이지 이동 시 페이지 이동 기록이 남아서 뒤로 가기 버튼을 누르면 이 페이지로 다시 돌아온다.
                        finish() // 액티비티 종료  // 페이지 이동 기록 남지 않는다.
                    })
                .setCancelable(false) // 뒤로 가기 버튼과 영역 외 클릭 시 Dialog가 사라지지 않도록 한다.
            // Dialog 띄워 주기
            builder.show()
        }

/*
        // 앱 종료
		System.exit(0) // 방법 1
    	android.os.Process.killProcess(android.os.Process.myPid()) // 방법 2
*/
    }
}