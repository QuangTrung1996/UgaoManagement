package com.ugao.ugaomanagement.activity

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.messaging.FirebaseMessaging
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.app.Config

@Suppress("DEPRECATION")
class LaunchScreenActivity : AppCompatActivity() {

    private lateinit var mRegistrationBroadCastReceiver: BroadcastReceiver
    private val SPLASH_TIME : Long = 3000

    lateinit var sharedPreferences: SharedPreferences
    val myPreference = "myPref"
    val idKey = "idKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_launch_screen)

        BackgroundTask().execute()

        FirebaseMessaging.getInstance().subscribeToTopic("store_owner")

        mRegistrationBroadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Config.STR_PUSH) {
                    val message = intent.getStringExtra("message")
                    showNotification("EDMTDev", message)
                }
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val b = NotificationCompat.Builder(applicationContext)
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent)
        val notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, b.build())
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadCastReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadCastReceiver,
                IntentFilter("registrationComplete"))
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadCastReceiver,
                IntentFilter(Config.STR_PUSH))
    }

    @SuppressLint("StaticFieldLeak")
    private inner class BackgroundTask : AsyncTask<String, String, Any>() {

        var intent: Intent? = null

        override fun onPreExecute() {
            super.onPreExecute()

            sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)

            intent = if (sharedPreferences.getString(idKey, "") != ""){
                Intent(this@LaunchScreenActivity, MainActivity::class.java)
            } else{
                Intent(this@LaunchScreenActivity, LoginActivity::class.java)
            }

//            intent = Intent(this@LaunchScreenActivity, NotificationActivity::class.java)
        }

        override fun doInBackground(vararg params: String?): Any? {

            try {
                Thread.sleep(SPLASH_TIME)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            startActivity(intent)
            finish()
        }
    }
}
