package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.app.ProgressDialog.STYLE_SPINNER
import android.app.SearchManager
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.adapter.InvoiceAdapter
import com.ugao.ugaomanagement.model.Invoice
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException
import org.json.JSONObject
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import com.ugao.ugaomanagement.activity.InvoiceDetailActivity
import com.ugao.ugaomanagement.activity.MainActivity

class InvoiceFragment : Fragment() {

//    private lateinit var searchView: SearchView
    private val TAG: String = InvoiceFragment::class.java.simpleName
    private var httpInvoiceAsyncTask: HttpInvoiceAsyncTask? = null
    val invoiceList = ArrayList<Invoice>()

    lateinit var sharedPreferences: SharedPreferences
    val myPreference = "myPref"
    val idKey = "idKey"

    var https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={store(id:%22"
    var query = "%22){invoices{_id,order_date,paid,price,payment_method}}}"

    private lateinit var listView : ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val roof = inflater.inflate(R.layout.fragment_invoice, container, false)
//        val actionBarToolBar : Toolbar = roof.findViewById(R.id.toolbar)
//        (activity as (MainActivity)).setSupportActionBar(actionBarToolBar)
//        actionBarToolBar.setTitle(R.string.title_invoice)
//        setHasOptionsMenu(true)

        listView = roof.findViewById(R.id.lv_invoice)

        sharedPreferences = activity!!.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        httpInvoiceAsyncTask = HttpInvoiceAsyncTask()
        httpInvoiceAsyncTask!!.execute(https + sharedPreferences.getString(idKey, "") + query)

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val intent = Intent(activity, InvoiceDetailActivity::class.java)
            intent.putExtra("message", invoiceList[position].id)
            intent.putExtra("message_order_date", invoiceList[position].order_date)

            if (invoiceList[position].paid){
                intent.putExtra("message_paid", "true")
            }
            else {
                intent.putExtra("message_paid", "false")
            }

            intent.putExtra("message_price", invoiceList[position].price.toString())

            activity!!.startActivity(intent)
        }

        return roof
    }

    @SuppressLint("StaticFieldLeak")
    inner class HttpInvoiceAsyncTask : AsyncTask<String, String, String>() {

        private var progressBar = ProgressDialog(activity)

        //Hàm này sẽ chạy đầu tiên khi AsyncTask này được gọi
        override fun onPreExecute() {
            super.onPreExecute()

            progressBar.setCancelable(false)
            progressBar.setMessage("Getting data...")
            progressBar.setProgressStyle( STYLE_SPINNER)
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

                // Lấy nút JSON Object store
                val store = jsonData.getJSONObject("store")

                // Lấy nút JSON Array
                val invoices = store.getJSONArray("invoices")

                // lặp qua tất cả liên hệ
                for (i in 0 until invoices.length()) {
                    val c = invoices.getJSONObject(i)
                    val invoice = Invoice()

                    invoice.id = c.getString("_id")
                    invoice.order_date = c.getString("order_date")
                    invoice.paid = c.getString("paid")!!.toBoolean()
                    invoice.price = c.getString("price").toInt()
                    invoice.payment_method = c.getString("payment_method")

                    invoiceList.add(invoice)
                }
            }
            catch (e: JSONException) {
                Log.e(TAG, "Json parsing error: " + e.message)
                activity!!.runOnUiThread( {
                    Toast.makeText(activity,
                            "Json parsing error: " + e.message,
                            Toast.LENGTH_LONG)
                            .show()
                })

            }

            listView.adapter = InvoiceAdapter(activity!!,invoiceList)

            progressBar.dismiss()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_main, menu)
//
//
//    }

}