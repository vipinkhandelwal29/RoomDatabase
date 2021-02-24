package com.example.roomdatabase.database.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.roomdatabase.MainActivity
import com.example.roomdatabase.R
import com.google.firebase.messaging.RemoteMessage


fun Context.notificationClass(remoteMessage: RemoteMessage) {
    val builder = NotificationCompat.Builder(this, getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_baseline_camera_24)
        .setContentTitle(remoteMessage.notification!!.title)
        .setContentText(remoteMessage.notification!!.body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationIntent = Intent(this, MainActivity::class.java)
    val contentIntent = PendingIntent.getActivity(
        this, 0, notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    builder.setContentIntent(contentIntent)


    val notificationManager: NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.add_word)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(getString(R.string.app_name), name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system

        notificationManager.createNotificationChannel(channel)
    }
    notificationManager.notify(0, builder.build())
}