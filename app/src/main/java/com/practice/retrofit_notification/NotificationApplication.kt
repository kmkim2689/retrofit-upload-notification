package com.practice.retrofit_notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationApplication : Application() {
    // notification channel
    override fun onCreate() {
        super.onCreate()
        // OREO부터 notification channel이 필요
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ProgressNotificationService.PROGRESS_CHANNEL_ID,
                "upload progress notification",
                NotificationManager.IMPORTANCE_LOW
            )
            // channel description -> 앱 정보에서 사용자에게 설명용
            channel.description = "to notify users of the upload progress"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}