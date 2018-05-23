package com.ugao.ugaomanagement.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "JSA-FCM"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        if (remoteMessage!!.notification != null) {
            Log.e(TAG, "Title: " + remoteMessage.notification!!.title!!)
            Log.e(TAG, "Body: " + remoteMessage.notification!!.body!!)
        }

        if (remoteMessage.data.size > 0) {
            Log.e(TAG, "Data: " + remoteMessage.data)
        }
    }
}