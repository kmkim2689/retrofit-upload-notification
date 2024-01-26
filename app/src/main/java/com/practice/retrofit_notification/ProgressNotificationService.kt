package com.practice.retrofit_notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.view.ContentInfoCompat.Flags

class ProgressNotificationService(
    private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val intent = Intent(context, MainActivity::class.java)
    private val pendingIntent = PendingIntent.getActivity(
        context,
        (System.currentTimeMillis() / 7).toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE // what should be done with this pending intent
    )

    private val progressIntent = Intent(context, ProgressNotificationReceiver::class.java)
    private val progressPendingIntent = PendingIntent.getBroadcast(
        context,
        1,
        progressIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    fun showProgressNotification(progress: Int, max: Int) {
        val notification = NotificationCompat.Builder(context, PROGRESS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("게시글 업로드 중")
            .setContentText("업로드 : ${progress}/${max}")
            .setProgress(max, progress, false)
//            .setStyle()
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        notificationManager.notify(
            1, // 만약 notify할 때마다 다른 아이디를 사용한다면, 업데이트 될 때마다 다른 notification이 나타난다.
            notification
        )

        if (progress == max) {
            showCompleteNotification()
        }
    }

    private fun showCompleteNotification() {
        val notification = NotificationCompat.Builder(context, PROGRESS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("게시글 업로드 완료")
            .setContentText("업로드가 완료되었습니다.")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(
            1, // 만약 notify할 때마다 다른 아이디를 사용한다면, 업데이트 될 때마다 다른 notification이 나타난다.
            notification
        )

        UploadProgress.max = 0
        UploadProgress.progress = 0
    }

    companion object {
        const val PROGRESS_CHANNEL_ID = "progress_channel"
        const val INTENT_MAIN = 0
    }
}