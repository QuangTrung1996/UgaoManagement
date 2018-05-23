package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.adapter.ShipperAdapter
import com.ugao.ugaomanagement.model.Shipper
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL

class ShipperFragment  : Fragment() {

    private lateinit var listView : ListView
    private var httpShipperAsyncTask: HttpShipperAsyncTask? = null
    val shipperList = ArrayList<Shipper>()

    lateinit var sharedPreferences: SharedPreferences
    val myPreference = "myPref"
    val idKey = "idKey"

    var https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={store(id:%22"
    var query = "%22){shippers {_id,name,phone,email,img}}}"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_shipper, container, false)

        listView = v.findViewById(R.id.list_shipper)

        sharedPreferences = activity!!.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        httpShipperAsyncTask = HttpShipperAsyncTask()
        httpShipperAsyncTask!!.execute(https + sharedPreferences.getString(idKey, "") + query)
        return v
    }

    @SuppressLint("StaticFieldLeak")
    inner class HttpShipperAsyncTask : AsyncTask<String, String, String>() {

        private var progressBar = ProgressDialog(activity)

        //Hàm này sẽ chạy đầu tiên khi AsyncTask này được gọi
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.setCancelable(false)
            progressBar.setMessage("Getting data...")
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressBar.isIndeterminate = true
            progressBar.show()
        }

        //Hàm được được hiện tiếp sau hàm onPreExecute()
        //Hàm này thực hiện các tác vụ chạy ngầm
        //Tuyệt đối k vẽ giao diện trong hàm này
        override fun doInBackground(vararg params: String?): String {
            val content = StringBuffer()
            val url = URL(params[0])
            val urlConnection: HttpURLConnection =url.openConnection()as HttpURLConnection
            val inputStreamReader = InputStreamReader(urlConnection.inputStream)
            val bufferReader = BufferedReader(inputStreamReader as Reader?)
            var line : String
            try {
                do {
                    line=bufferReader.readLine()
                    if (line!=null){
                        content.append(line)
                    }

                }while (line != null)
                bufferReader.close()

            }catch (e:Exception){
                Log.d("AAA",e.toString())
            }

            return content.toString()
        }

        //Hàm này được thực hiện khi tiến trình kết thúc
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                val jsonObj = JSONObject(result)

                // data node is JSON Object
                val jsonData = jsonObj.getJSONObject("data")

                // data node is JSON Object Store
                val jsonStore = jsonData.getJSONObject("store")

                // Lấy nút JSON Array
                val shippers = jsonStore.getJSONArray("shippers")

                // lặp qua tất cả liên hệ
                for (i in 0 until shippers.length()) {
                    val c = shippers.getJSONObject(i)
                    val shipper = Shipper()

                    shipper.id = c.getString("_id")
                    shipper.name = c.getString("name")
                    shipper.phone = c.getString("phone")
                    shipper.email = c.getString("email")
                    shipper.img = c.getString("img")

                    shipperList.add(shipper)
                }
            }
            catch (e: JSONException) {

                activity!!.runOnUiThread( {
                    Toast.makeText(activity,
                            "Json parsing error: " + e.message,
                            Toast.LENGTH_LONG)
                            .show()
                })

            }

            listView.adapter = ShipperAdapter(activity!!,shipperList)

            progressBar.dismiss()
        }
    }
}