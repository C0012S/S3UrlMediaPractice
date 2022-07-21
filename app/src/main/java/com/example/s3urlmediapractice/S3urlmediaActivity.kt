package com.example.s3urlmediapractice

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.ImageView
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_s3urlmedia.*

class S3urlmediaActivity : AppCompatActivity(), SurfaceHolder.Callback {

    var defaultImage = R.drawable.default_poster

    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s3urlmedia)

        var imageView = findViewById<ImageView>(R.id.image)

        Log.d("실행 : ", "S3urlmediaActivity")

        Thread {
            val bucketUrl : String? = ensureBucketExists("bucket_Name")
            if (!(bucketUrl.isNullOrBlank())) {
                Log.d("bucketUrl : ", bucketUrl)

                /*
                // java.lang.IllegalArgumentException : You must call this method on the main thread
                Glide.with(applicationContext)
                    .load(bucketUrl + "image_Name.jpg") // 불러올 이미지 url
                    .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                    .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                    .fallback(defaultImage) // 로드할 url이 비어있을(null 등) 경우 표시할 이미지
                    .into(imageView) // 이미지를 넣을 뷰
                */

                runOnUiThread {
                    Glide.with(applicationContext)
                        .load(bucketUrl + "image_Name.jpg")
                        .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                        .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                        .fallback(defaultImage) // 로드할 url이 비어있을(null 등) 경우 표시할 이미지
                        .into(imageView)

                    Log.d("ImageView : ", bucketUrl + "image_Name.jpg")
                }
            }
        }.start()

        // 동영상 파일 URL(videoUrl) 얻어 와서 사용  // S3 Bucket 안의 동영상 파일 객체 URL(bucketUrl + videoName) 얻어 와서 사용
        // s3Client.doesBucketExist(bucketName) 사용 시 - E/SurfaceView: Exception configuring surface  android.os.NetworkOnMainThreadException
        mediaPlayer = MediaPlayer()
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    private fun ensureBucketExists(bucketName : String) : String? {
        var bucketUrl : String? = null
        val awsCredentials : AWSCredentials = BasicAWSCredentials("access_Key", "secret_Key") // IAM User의 (accessKey, secretKey)
        val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

/*
    try {
        if (!s3Client.doesBucketExist(bucketName)) {
            Log.d("Bucket : ", "Doesn't exists")

            // S3 Bucket Create
            //s3Client.createBucket(bucketName)
            //Log.d("Bucket : ", "Created - " + bucketName)
        }

        bucketUrl = s3Client.getResourceUrl(bucketName, null).toString() + bucketName
    }
    catch (ace : AmazonClientException) {
        Log.d("Error : ", "An error occurred while connecting to S3. Will not execute action for bucket - " + bucketName + ", " + ace)
        throw S3Exception(ace)
    }
*/
        if (!s3Client.doesBucketExist(bucketName)) {
            Log.d("Bucket : ", "Doesn't exists")

            // S3 Bucket Create
            //s3Client.createBucket(bucketName)
            //Log.d("Bucket : ", "Created - " + bucketName)
        }

        bucketUrl = s3Client.getResourceUrl(bucketName, null).toString()
        Log.d("getResourceUrl : ", s3Client.getResourceUrl(bucketName, null).toString())

        return bucketUrl
    }

    override fun surfaceCreated(holder : SurfaceHolder) {
        var bucketUrl : String? = null
        val awsCredentials : AWSCredentials = BasicAWSCredentials("access_Key", "secret_Key") // IAM User의 (accessKey, secretKey)
        val s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))
        bucketUrl = s3Client.getResourceUrl("bucket_Name", null).toString()
//        val bucketUrl : String? = ensureBucketExists("bucket_Name")  // s3Client.doesBucketExist(bucketName) 사용 시 - E/SurfaceView: Exception configuring surface  android.os.NetworkOnMainThreadException
        var videoName : String? = "video_Name.mp4"
//        val videoUrl : String? = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

        try {
            mediaPlayer.setDataSource(bucketUrl + videoName)
//            mediaPlayer.setDataSource(videoUrl)
            mediaPlayer.setDisplay(holder)
            mediaPlayer.prepare()
            mediaPlayer.start()

            Log.d("MediaPlayer : ", bucketUrl + videoName + " 재생")
//            Log.d("MediaPlayer : ", "재생")
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

class S3Exception : RuntimeException {
    constructor(e : Throwable?) : super(e) {}
    constructor(s : String?) : super(s) {}
}