package com.japo.selfiepoc.client.body

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayInputStream

class ProgressSelfieBody(val mFile: ByteArray) : RequestBody() {

    var mListener: UploadCallbacks? = null

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError() {}
        fun onFinish() {}
    }

    fun setUpListener(listener: UploadCallbacks) {
        mListener = listener
    }

    override fun contentType() = MediaType.parse("image/png")

    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.size
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        val inputStream = ByteArrayInputStream(mFile)
        var uploaded: Long = 0

        var read = 0

        try {
            val handler = Handler(Looper.getMainLooper())
            while ({ read = inputStream.read(buffer); read }() != -1) {
                sink.write(buffer, 0, read)
                uploaded += read
                handler.post(ProgressUpdater(uploaded, fileLength))
            }
        } catch (e: Exception) {
            mListener?.onError()
        } finally {
            mListener?.onFinish()
        }
    }

    inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Int) : Runnable {
        override fun run() {
            val progress = (100 * mUploaded / mTotal).toInt()
            mListener?.onProgressUpdate(progress)
        }
    }

}

