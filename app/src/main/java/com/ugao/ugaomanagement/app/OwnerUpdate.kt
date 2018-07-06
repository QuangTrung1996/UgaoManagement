package com.ugao.ugaomanagement.app

import android.util.Log
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class OwnerUpdate() {

    lateinit var id : String

    //  post thong tin thay doi
    lateinit var postNameOwner : String
    lateinit var postPhone : String
    lateinit var postPass : String
    lateinit var postImage : String
    lateinit var postToken : String

    fun pushUpdate(type: String) {

        val thread = Thread(Runnable {
            try {
                val url = URL("http://gentle-dawn-11577.herokuapp.com/store/owner/update")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                jsonParam.put("_id", id)

                when (type) {
                    "owner" -> {
                        jsonParam.put("name", postNameOwner)
                        jsonParam.put("phone", postPhone)
                    }
                    "pass" -> jsonParam.put("pass", postPass)
                    "image" -> jsonParam.put("img", postImage)
                    "token" -> jsonParam.put("token", postToken)
                }

                Log.i("JSON", jsonParam.toString())
                val os = DataOutputStream(conn.outputStream)
                os.writeBytes(jsonParam.toString())

                os.flush()
                os.close()

                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)

                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        thread.start()
    }
}