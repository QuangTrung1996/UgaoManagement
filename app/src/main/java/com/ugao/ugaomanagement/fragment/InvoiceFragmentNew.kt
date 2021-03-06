package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.app.ProgressDialog.STYLE_SPINNER
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.ugao.ugaomanagement.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException
import org.json.JSONObject
import android.content.SharedPreferences
import android.view.*
import com.ugao.ugaomanagement.adapter.InvoicePaperAdapterNew
import com.ugao.ugaomanagement.app.Config.myPreference
import com.ugao.ugaomanagement.app.Config.storeKey
import com.ugao.ugaomanagement.internet.CheckInternet
import com.ugao.ugaomanagement.internet.CheckInternetInterface
import com.ugao.ugaomanagement.model.Invoice
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class InvoiceFragmentNew : Fragment(), CheckInternetInterface {

    private var isConnected = true

    //    private lateinit var searchView: SearchView
    private val TAG: String = InvoiceFragmentNew::class.java.simpleName
    private var httpInvoiceAsyncTask: HttpInvoiceAsyncTask? = null

    val invoiceListNew = ArrayList<Invoice>()
    val invoiceListDelivered = ArrayList<Invoice>()
    val invoiceListComplete = ArrayList<Invoice>()

    lateinit var sharedPreferences: SharedPreferences

    var https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={store(id:%22"
    var query = "%22){invoices{_id,order_date,paid,price,payment_method,shipper{name},tasks{receipt_date}}}}"

    private lateinit var listView : ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val roof = inflater.inflate(R.layout.fragment_invoice_list, container, false)

        listView = roof.findViewById(R.id.lv_invoice)

        invoiceListNew.clear()
        invoiceListDelivered.clear()
        invoiceListComplete.clear()

        sharedPreferences = activity!!.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        val checkInternet = CheckInternet(this)
        checkInternet.checkConnection(activity!!)
        if (isConnected) {
            httpInvoiceAsyncTask = HttpInvoiceAsyncTask()
            httpInvoiceAsyncTask!!.execute(https + sharedPreferences.getString(storeKey, "") + query)
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
                    invoice.orderDate = c.getString("order_date")
                    invoice.paid = c.getString("paid")!!.toBoolean()
                    invoice.price = c.getString("price")
                    invoice.paymentMethod = c.getString("payment_method")

                    val nameShipper = try{
                        val shipper = c.getJSONObject("shipper")
                        shipper.getString("name")
                    } catch (e: JSONException){
                        ""
                    }

                    if (nameShipper == ""){
//                        addList(invoiceListNew,invoice)
                        invoiceListNew.add(invoice)
                    }
                    else if (!invoice.paid){
//                        addList(invoiceListDelivered,invoice)
                        invoiceListDelivered.add(invoice)
                    }
                    else {
                        try{
                            val tasks = c.getJSONObject("tasks")
                            invoice.orderDate = tasks.getString("receipt_date")
                        } catch (e: JSONException){

                        }
//                        addList(invoiceListComplete,invoice)
                        invoiceListComplete.add(invoice)
                    }
                }
            }
            catch (e: JSONException) {
                Log.e(TAG, "Json parsing error: " + e.message)
                activity!!.runOnUiThread {
                    Toast.makeText(activity, "Lỗi Json.", Toast.LENGTH_LONG).show()
                }
            }

            listView.adapter = InvoicePaperAdapterNew(activity!!,invoiceListNew)

            InvoiceFragmentDelivered.invoiceListDelivered = invoiceListDelivered
            InvoiceFragmentComplete.invoiceListComplete = invoiceListComplete

            progressBar.dismiss()
        }
    }

    private fun addList(list: ArrayList<Invoice>, invoice: Invoice) {
        if(list.size == 0){
            //1st item, add it on loc=0
            list.add(invoice)
        }else{
            val time = getTimeLong(invoice.orderDate)

            if(time > getTimeLong(list[0].orderDate)){
                list.add(0,invoice)
            }else{
                list.add(invoice)
            }
        }
    }

    private fun getTimeLong(dateTime: String): Long {
        if(dateTime.trim() == ""){
            return -1
        }

        try {
            val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.US)
            df.timeZone = TimeZone.getTimeZone("GMT+0000 (UTC)")
            val date = df.parse(dateTime)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            return -1;
        }
    }

    override fun checkInternet(isConnected: Boolean) {
        this.isConnected = isConnected
        showToast(isConnected)
    }

    // Showing the status in Toast
    private fun showToast(isConnected : Boolean) {
        if (!isConnected) {
            Toast.makeText(activity,"Sorry! Not connected to internet",Toast.LENGTH_LONG).show()
        }
    }
}