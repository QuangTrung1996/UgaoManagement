package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.app.ProgressDialog.STYLE_HORIZONTAL
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
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.adapter.StorageAdapter
import com.ugao.ugaomanagement.app.Config.myPreference
import com.ugao.ugaomanagement.app.Config.storeKey
import com.ugao.ugaomanagement.model.Storage
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL

class StorageFragment : Fragment() {

    lateinit var listView : ListView
    val storageProductList = ArrayList<Storage>()

    private var httpStorageAsyncTask: HttpStorageAsyncTask? = null

    lateinit var sharedPreferences: SharedPreferences

    var https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={store(id:%22"
    var query = "%22){storage{amount,receipt_date,product {_id,name,img,price,weight}}}}"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_storage, container, false)
        sharedPreferences = activity!!.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        listView = v.findViewById(R.id.list_storage_product)

        httpStorageAsyncTask = HttpStorageAsyncTask()
        httpStorageAsyncTask!!.execute(https + sharedPreferences.getString(storeKey, "") + query)

        return v
    }

    @SuppressLint("StaticFieldLeak")
    inner class HttpStorageAsyncTask : AsyncTask<String, String, String>() {

        private var progressBar = ProgressDialog(activity)

        //Hàm này sẽ chạy đầu tiên khi AsyncTask này được gọi
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.setCancelable(false)
            progressBar.setMessage("Getting data...")
            progressBar.setProgressStyle(STYLE_HORIZONTAL)
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

//            txt.text = result
            val jsonObj = JSONObject(result)

            // data node is JSON Object
            val jsonData = jsonObj.getJSONObject("data")

            // Lấy nút JSON Object store
            val store = jsonData.getJSONObject("store")

            // Lấy nút JSON Array
            val storage = store.getJSONArray("storage")

            // lặp qua tất cả liên hệ
            for (i in 0 until storage.length()) {
                val c = storage.getJSONObject(i)

                val storageProduct = Storage()

                storageProduct.amount = c.getString("amount").toFloat()
                storageProduct.receipt_date = c.getString("receipt_date")

                // Lấy nút JSON Object invoice
                val product = c.getJSONObject("product")
                storageProduct.product.id = product.getString("_id")
                storageProduct.product.name = product.getString("name")

                if (product.getString("weight") == "null"){
                    storageProduct.product.weight = 0.5f
                }
                else{
                    storageProduct.product.weight = product.getString("weight").toFloat()
                }

//                storageProduct.product.weight = product.getString("weight").toFloat()
                storageProduct.product.img = product.getString("img")
                storageProduct.product.price = product.getString("price").toFloat()

                storageProductList.add(storageProduct)
            }

            listView.adapter = StorageAdapter(activity!!,storageProductList)

            progressBar.dismiss()
        }
    }
}