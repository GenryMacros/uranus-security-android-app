package com.example.uranus.ui.invasions_page.utility

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.uranus.R
import com.example.uranus.ui.broadcast.BroadcastActivity
import com.example.uranus.ui.home_page.data.InvasionPayload


class Notificator(
    val context: Context
) {
    private val channelId: String = "INVASIONS"
    private val channelName: String = "INVASIONS_CHANNEL"
    private lateinit var channel: NotificationChannel
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private var nextNotificationId: Int = 0
    private var isInited: Boolean = false

    fun init() {
        if (isInited) {
            return
        }
        createNotificationChannel()
        isInited = true
    }
    fun sendNotification(data: InvasionPayload) {
        if (!isInited) {
            return
        }
        val intent = Intent(
            context,
            BroadcastActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationUri: Uri = Uri.parse("file:///android_asset/notification.mp3")
        with(NotificationManagerCompat.from(context)) {
            builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.cam)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("Potential invasion!")
                .setContentText(data.date + " Cam: " + data.cam_id)
                .setSound(notificationUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            notify(nextNotificationId, builder.build())
        }
        nextNotificationId++
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(channel)
        }
    }

}