package com.japo.selfiepoc

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraAccessException
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.japo.selfiepoc.client.body.ProgressSelfieBody
import com.japo.selfiepoc.client.response.IdentityVerificationResponse
import kotlinx.android.synthetic.main.activity_selfie.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class SelfieActivity : AppCompatActivity() {

    companion object {
        const val TAKE_PHOTO_REQUEST = 101
        const val MY_PERMISSIONS_REQUEST_CAMERA = 1242
        const val MY_PERMISSIONS_REQUEST_WRITE = 1342
        const val MY_PERMISSIONS_REQUEST_INTERNET = 1442
    }

    private var mCurrentPhotoPath: String = ""
    private var file: File? = null
    private val app = MyApplication.instance
    private val service = app.service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfie)
        draweeView?.setOnClickListener { validatePermissions() }
        sendImageButton?.setOnClickListener { checkNetwork() }
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

    private fun checkNetwork() {
        checkPermission(Manifest.permission.INTERNET, MY_PERMISSIONS_REQUEST_INTERNET, this::sendCurrentImage)
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
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
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
        file = File(photoPath)
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

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else if (bitmapRatio > 0) {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    // SEND IMAGE
    private fun sendCurrentImage() {
        progressBar.visibility = View.VISIBLE
        uploadProgress.visibility = View.VISIBLE
        progressBar.progress = 0
        if (file != null) {
            try {
                val bitmap = getResizedBitmap(BitmapFactory.decodeFile(file?.path), 600)
                val byteArray = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArray)
                val body = ProgressSelfieBody(byteArray.toByteArray())
                body.setUpListener(object : ProgressSelfieBody.UploadCallbacks {
                    override fun onProgressUpdate(percentage: Int) {
                        progressBar.progress = percentage
                    }

                    override fun onFinish() {
                        this@SelfieActivity.runOnUiThread {
                            progressBar.visibility = View.GONE
                        }
                    }
                })

                if (app.verificationId != null) {
                    service.postSelfie(app.verificationId!!, body).enqueue(object : Callback<IdentityVerificationResponse> {
                        override fun onResponse(call: Call<IdentityVerificationResponse>?, response: Response<IdentityVerificationResponse>?) {
                            println(response?.body()?.nextStep)
                            uploadProgress.visibility = View.GONE
                        }

                        override fun onFailure(call: Call<IdentityVerificationResponse>?, t: Throwable?) {
                            uploadProgress.visibility = View.GONE
                        }
                    })

                }
            } catch (e: Exception) {
                println(e.printStackTrace())
            }
        }

    }
}
