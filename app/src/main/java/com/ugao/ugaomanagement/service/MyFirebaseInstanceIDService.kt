package com.ugao.ugaomanagement.service

import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    val FIREBASE_TOKEN = "firebase token"
    private val TAG = "MyFirebaseIIDService"

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Toast.makeText(applicationContext,refreshedToken,Toast.LENGTH_SHORT).show()

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.edit().putString(FIREBASE_TOKEN, refreshedToken).apply()
    }
}