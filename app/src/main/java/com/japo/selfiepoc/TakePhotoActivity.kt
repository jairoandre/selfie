package com.japo.selfiepoc

import android.content.Intent
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity

class TakePhotoActivity : AppCompatActivity() {
    // https://developer.android.com/training/camera/photobasics.html
    // http://www.codepool.biz/take-a-photo-from-android-camera-and-upload-it-to-a-remote-php-server.html

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
