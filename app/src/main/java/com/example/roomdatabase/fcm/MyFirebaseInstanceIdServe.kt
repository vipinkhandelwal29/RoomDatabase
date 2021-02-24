package com.example.roomdatabase.fcm

import android.content.Intent
import android.util.Log
import com.example.roomdatabase.MainActivity
import com.example.roomdatabase.database.util.notificationClass
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseInstanceIdServe : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("==>onNewToken:", " $token")
        val pref = applicationContext.getSharedPreferences("MyPref", 0)
        pref.edit().putString("token", token).apply()
        super.onNewToken(token)


    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        notificationClass(remoteMessage)
        //sendNotification(remoteMessage)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("message", remoteMessage.notification!!.body!!)
        startActivity(intent)
        Log.d("==>", "onMessageReceived: ${remoteMessage}")

        Log.d("==>", "From: " + remoteMessage.from)
        Log.d("==>", "Notification Message Body: " + remoteMessage.notification!!.body!!)
    }


}




