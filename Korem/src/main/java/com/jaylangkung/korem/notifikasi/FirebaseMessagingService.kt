package com.jaylangkung.korem.notifikasi

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Firebase.messaging.token
//    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["message"]

        NotificationHelper(applicationContext).displayNotification(title!!, body!!)
    }
}