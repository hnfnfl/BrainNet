package com.jaylangkung.brainnet_staff.utils

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]

        NotificationHelper(applicationContext).displayNotification(title!!, body!!)
    }
}