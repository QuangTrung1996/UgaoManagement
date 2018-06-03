package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ugao.ugaomanagement.R
import android.content.Intent.getIntent
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import android.os.Looper
import android.widget.Button
import com.ugao.ugaomanagement.app.Config
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@Suppress("DEPRECATION")
class PlusOneFragment : Fragment() {

    private lateinit var mTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_plus_one, container, false)

        mTextView = view.findViewById(R.id.txt_result)

        val bundle = getIntent("").extras
        if (bundle != null) {
            var tmp = ""
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                tmp += "$key: $value\n\n"
            }
            mTextView.text = tmp
        }

        val showToken : Button = view.findViewById(R.id.showToken)
        val subscribe : Button = view.findViewById(R.id.subscribe)
        val unsubscribe : Button = view.findViewById(R.id.unsubscribe)
        val sendToken : Button = view.findViewById(R.id.sendToken)
        val sendTokens : Button = view.findViewById(R.id.sendTokens)
        val sendTopic : Button = view.findViewById(R.id.sendTopic)

        showToken.setOnClickListener { showToken() }
        subscribe.setOnClickListener { subscribe() }
        unsubscribe.setOnClickListener { unsubscribe() }
        sendToken.setOnClickListener { sendToken() }
        sendTokens.setOnClickListener { sendTokens() }
        sendTopic.setOnClickListener { sendTopic() }

        return view
    }

    fun showToken() {
        mTextView.text = FirebaseInstanceId.getInstance().token
        Log.i("token", FirebaseInstanceId.getInstance().token)
    }

    @SuppressLint("SetTextI18n")
    fun subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("news")
        mTextView.text = "subscribed"
    }

    @SuppressLint("SetTextI18n")
    fun unsubscribe() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("news")
        mTextView.text = "unSubscribed"
    }

    fun sendToken() {
        sendWithOtherThread("token")
    }

    fun sendTokens() {
        sendWithOtherThread("tokens")
    }

    fun sendTopic() {
        sendWithOtherThread("topic")
    }

    private fun sendWithOtherThread(type: String) {
        Thread(Runnable { pushNotification(type) }).start()
    }

    private fun pushNotification(type: String) {
        val jPayload = JSONObject()
        val jNotification = JSONObject()
        val jData = JSONObject()
        try {
            jNotification.put("title", "Lấy thêm gạo")
            jNotification.put("body", "Firebase Cloud Messaging (App)")
            jNotification.put("sound", "default")
            jNotification.put("badge", "1")
            jNotification.put("click_action", "OPEN_ACTIVITY_1")
            jNotification.put("icon", "ic_notification")

            jData.put("picture", "http://opsbug.com/static/google-io.jpg")

            when (type) {
                "tokens" -> {
                    val ja = JSONArray()
                    ja.put("e69hnI7EZDQ:APA91bHPjmWNzF5P0trwlO328esm34SSKedBmtK7VznaFLk-kg9GT2aVkrCcFRZz7GE3vHuk-4TF0xP_8tnW8GojcQ-tnAz7mPXYhI31_7XCR2TILKKx6pr7JbWX3VkTetylj3R4BIzA")
                    ja.put(FirebaseInstanceId.getInstance().token)
                    jPayload.put("registration_ids", ja)
                }
                "topic" -> jPayload.put("to", "/topics/news")
                "condition" -> jPayload.put("condition", "'sport' in topics || 'news' in topics")
                else -> jPayload.put("to", FirebaseInstanceId.getInstance().token)
            }

            jPayload.put("priority", "high")
            jPayload.put("notification", jNotification)
            jPayload.put("data", jData)

            val url = URL("https://fcm.googleapis.com/fcm/send")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", Config.AUTH_KEY)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            // Send FCM message content.
            val outputStream = conn.outputStream
            outputStream.write(jPayload.toString().toByteArray())

            // Read FCM response.
            val inputStream = conn.inputStream
            val resp = convertStreamToString(inputStream)

            val h = Handler(Looper.getMainLooper())
            h.post({ mTextView.text = resp })
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun convertStreamToString(`is`: InputStream): String {
        val s = Scanner(`is`).useDelimiter("\\A")
        return if (s.hasNext()) s.next().replace(",", ",\n") else ""
    }

}