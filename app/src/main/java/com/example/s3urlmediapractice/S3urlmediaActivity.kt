package com.example.s3urlmediapractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide

class S3urlmediaActivity : AppCompatActivity() {

    var defaultImage = R.drawable.default_poster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s3urlmedia)

        var imageView = findViewById<ImageView>(R.id.image)

        Thread {
            val bucketUrl : String? = ensureBucketExists("bucket_Name")
            if (!(bucketUrl.isNullOrBlank())) {
                Log.d("bucketUrl : ", bucketUrl)

                /* java.lang.IllegalArgumentException : You must call this method on the main thread
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

        Log.d("실행 : ", "S3urlmediaActivity")
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
}

class S3Exception : RuntimeException {
    constructor(e : Throwable?) : super(e) {}
    constructor(s : String?) : super(s) {}
}