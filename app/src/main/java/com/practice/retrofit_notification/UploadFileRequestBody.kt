package com.practice.retrofit_notification

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

interface UploadCallback {
    fun onProgressUpdate(percentage: Int)
    fun onFinish()
    fun onError()
}

class UploadBody(
    private val file: File?,
    private val requestBody: RequestBody?,
    private val contentType: String,
    private val callback: UploadCallback
) : RequestBody() {

    inner class ProgressUpdate(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
            callback.onProgressUpdate((uploaded / total * 100).toInt())
        }
    }

    constructor(
        requestBody: RequestBody,
        contentType: String,
        callback: UploadCallback
    ) : this(
        file = null,
        requestBody = requestBody,
        contentType = contentType,
        callback = callback
    )

    constructor(
        file: File,
        contentType: String,
        callback: UploadCallback
    ) : this(
        file = file,
        requestBody = null,
        contentType = contentType,
        callback = callback
    )

    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()

    override fun contentLength(): Long = file?.length() ?: (requestBody?.contentLength() ?: 0)

    override fun writeTo(sink: BufferedSink) {
        // write all the contents or the request into the bufferedSink
        if (file != null) {
            val length = file.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

            val fileInputStream = FileInputStream(file)
            // currently uploaded bytes that are uploaded(upload status)
            var uploaded = 0L
            fileInputStream.use { fis ->
                var read: Int
                val handler = Handler(Looper.getMainLooper())

                try {
                    // read all the contents from the file inputstream
                    while (fis.read(buffer).also { read = it } != -1) {
                        handler.post(ProgressUpdate(uploaded, length))
                        uploaded += read // read : 읽힌 만큼을 더함
                        sink.write(buffer, 0, read)
                    }
                } catch (e: Exception) {
                    callback.onError()
                }

                callback.onFinish()
            }
        }

        if (requestBody != null) {
            // write all the contents or the request into the bufferedSink
            val length = requestBody.contentLength()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

            // currently uploaded bytes that are uploaded(upload status)
            var uploaded = 0L

            try {
                requestBody.writeTo(sink)
            } catch (e: Exception) {
                callback.onError()
            }

            callback.onFinish()
        }

    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048 // 2MB
    }
}

//class UploadFileRequestBody(
//    private val file: File,
//    private val contentType: String,
//    private val callback: UploadCallback
//) : RequestBody() {
//
//    inner class ProgressUpdate(
//        private val uploaded: Long,
//        private val total: Long
//    ) : Runnable {
//        override fun run() {
//            callback.onProgressUpdate((uploaded / total * 100).toInt())
//        }
//    }
//
//    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()
//
//    override fun contentLength(): Long = file.length()
//
//    override fun writeTo(sink: BufferedSink) {
//        // write all the contents or the request into the bufferedSink
//        val length = file.length()
//        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//
//        val fileInputStream = FileInputStream(file)
//        // currently uploaded bytes that are uploaded(upload status)
//        var uploaded = 0L
//        fileInputStream.use { fis ->
//            var read: Int
//            val handler = Handler(Looper.getMainLooper())
//
//            try {
//                // read all the contents from the file inputstream
//                while (fis.read(buffer).also { read = it } != -1) {
//                    handler.post(ProgressUpdate(uploaded, length))
//                    uploaded += read // read : 읽힌 만큼을 더함
//                    sink.write(buffer, 0, read)
//                }
//            } catch (e: Exception) {
//                callback.onError()
//            }
//
//            callback.onFinish()
//        }
//    }
//
//    companion object {
//        private const val DEFAULT_BUFFER_SIZE = 2048 // 2MB
//    }
//}
