package com.practice.retrofit_notification

import okhttp3.RequestBody
import java.io.File

fun getUploadRequestBody(
    file: File,
    contentType: String = "image/*",
    callback: UploadCallback,
) = UploadBody(
    file, contentType, callback
)

fun getUploadRequestBody(
    requestBody: RequestBody,
    contentType: String = "application/json",
    callback: UploadCallback,
) = UploadBody(
    requestBody, contentType, callback
)