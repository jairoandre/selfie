package com.japo.selfiepoc

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private val TAKE_PHOTO_REQUEST = 101
    private val MY_PERMISSIONS_REQUEST_CAMERA = 1242
    private val MY_PERMISSIONS_REQUEST_WRITE = 1342

    private var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        draweeView?.setOnClickListener { validatePermissions() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkPermission(feature: String, myPermissions: Int, function: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, feature) == PackageManager.PERMISSION_GRANTED)
            function()
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, feature)) {
            ActivityCompat.requestPermissions(this, arrayOf(feature), myPermissions)
            Toast.makeText(applicationContext, "request permission", Toast.LENGTH_SHORT).show()
        }
    }


    private fun validatePermissions() {
        checkPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA, this::checkWrite)
    }

    private fun checkWrite() {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE, this::launchCamera)

    }

    private fun launchCamera() {
        try {
            val values = ContentValues(1)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            val fileUri = contentResolver
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                mCurrentPhotoPath = fileUri.toString()
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(intent, TAKE_PHOTO_REQUEST)
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) { android.provider.MediaStore.Images.ImageColumns.DATA },
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)

        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)

        val request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(ResizeOptions(width, height))
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(draweeView?.controller)
                .setImageRequest(request)
                .build()
        draweeView?.controller = controller
    }
}
