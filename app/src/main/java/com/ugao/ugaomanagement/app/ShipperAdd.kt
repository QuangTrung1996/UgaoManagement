package com.ugao.ugaomanagement.app

import android.content.Context
import android.util.Log
import com.ugao.ugaomanagement.model.Shipper
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.app.Activity
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import com.ugao.ugaomanagement.activity.ShipperAddActivity


class ShipperAdd() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharedPreferences

    fun postAddShipper(shipper: Shipper, con : Context) {

        sharedPreferences = con.getSharedPreferences(Config.myPreference, Context.MODE_PRIVATE)

        progressDialog = ProgressDialog(con)
        progressDialog.setCancelable(false)

        progressDialog.setMessage("Logging you in...")
        showDialog()

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
                jsonParam.put("storeId", sharedPreferences.getString(Config.storeKey, ""))

                Log.i("JSON", jsonParam.toString())
                val os = DataOutputStream(conn.outputStream)
                os.writeBytes(jsonParam.toString())

                os.flush()
                os.close()

                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)

                conn.disconnect()

                hideDialog()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        thread.start()

        (con as Activity).finish()
    }

    private fun showDialog() {
        if (!progressDialog.isShowing)
            progressDialog.show()
    }

    private fun hideDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }
}