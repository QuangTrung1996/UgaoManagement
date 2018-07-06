package com.ugao.ugaomanagement.app

import android.util.Log
import com.ugao.ugaomanagement.model.Shipper
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ShipperAdd() {

    fun postAddShipper(shipper: Shipper) {

        val thread = Thread(Runnable {
            try {
                val url = URL("http://gentle-dawn-11577.herokuapp.com/shipper/add")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                jsonParam.put("name", shipper.name)
                jsonParam.put("phone", shipper.phone)
                jsonParam.put("address", shipper.address)
                jsonParam.put("email", shipper.email)
                jsonParam.put("pass", shipper.pass)

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