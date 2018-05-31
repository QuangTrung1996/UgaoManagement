package com.ugao.ugaomanagement.activity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.messaging.FirebaseMessaging
import com.ugao.ugaomanagement.R

@Suppress("DEPRECATION")
class LaunchScreenActivity : AppCompatActivity() {

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
