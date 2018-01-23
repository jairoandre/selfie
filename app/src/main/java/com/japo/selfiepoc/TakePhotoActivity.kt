package com.japo.selfiepoc

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import kotlinx.android.synthetic.main.take_photo_activity.*

class TakePhotoActivity : AppCompatActivity() {
    // https://developer.android.com/training/camera/photobasics.html
    // http://www.codepool.biz/take-a-photo-from-android-camera-and-upload-it-to-a-remote-php-server.html
    lateinit var mOpenCameraBtn: Button
    lateinit var mSendPictureBtn: Button
    lateinit var mImageView: ImageView


    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.take_photo_activity)
        surfaceView.setOnClickListener { println("CLICKED") }
        mOpenCameraBtn = openCameraButton
        mSendPictureBtn = sendImageButton
        mOpenCameraBtn.setOnClickListener { dispatchTakePictureIntent() }
        mImageView = surfaceView
        mImageView.scaleType = ImageView.ScaleType.FIT_XY

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            surfaceView.setImageBitmap(imageBitmap)
        }
    }

}
