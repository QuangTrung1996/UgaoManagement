package com.ugao.ugaomanagement.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.activity.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFireBaseMsgService"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.i(TAG, "From: " + remoteMessage!!.from!!)
        Log.i(TAG, "Notification Message Body: " + remoteMessage.notification!!.body!!)
        Log.i(TAG, "Notification Message Title: " + remoteMessage.notification!!.title!!)

        sendNotification(remoteMessage.notification!!.body, remoteMessage.notification!!.title)
    }

    private fun sendNotification(messageBody: String?, messageTitle: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}