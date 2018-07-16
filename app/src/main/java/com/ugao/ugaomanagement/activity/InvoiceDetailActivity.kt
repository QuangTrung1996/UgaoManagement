package com.ugao.ugaomanagement.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.adapter.ProductAndQuantityAdapter
import com.ugao.ugaomanagement.model.ProductAndQuantity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONException

class InvoiceDetailActivity : AppCompatActivity() {

    private lateinit var txtId : TextView
    private lateinit var txtOrderDate : TextView
    private lateinit var txtPrice : TextView
    private lateinit var txtPaid : TextView
    lateinit var txtShipper : TextView
    lateinit var txtCustomer : TextView
    lateinit var listView : ListView

    var toolbar: Toolbar? = null
    private var httpInvoiceDetailAsyncTask: HttpInvoiceDetailAsyncTask? = null
    private var https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={invoice(id:%22"
    private var query = "%22){products{quantity,product{name,img,price,weight}},shipper{name},customer{name}}}"

    val productList = ArrayList<ProductAndQuantity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_detail)

        toolbar = findViewById(R.id.toolbar_elevated)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            //Hiện nút back
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        httpInvoiceDetailAsyncTask = HttpInvoiceDetailAsyncTask()
        httpInvoiceDetailAsyncTask!!.execute(https + intent.getStringExtra("message") + query)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        txtId        = findViewById(R.id.txt_id)
        txtOrderDate = findViewById(R.id.txt_order_date)
        txtPrice     = findViewById(R.id.txt_price)
        txtPaid      = findViewById(R.id.txt_paid)
        txtShipper   = findViewById(R.id.txt_shipper)
        txtCustomer  = findViewById(R.id.txt_customer)
        listView     = findViewById(R.id.lv_product)

        txtId.text   = " " + intent.getStringExtra("message")

        // doi lai thoi gian
        val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.US)
        val df1 = SimpleDateFormat("HH:mm:ss   dd-MM-yyyy", Locale.US)
        df.timeZone = TimeZone.getTimeZone("GMT+0000 (UTC)")
        val date = df.parse(intent.getStringExtra("message_order_date"))

        txtOrderDate.text   = df1.format(date)

        txtPrice.text       = (intent.getStringExtra("message_price").toFloat() * 1000).toInt().toString() + " Đ"

        if (intent.getStringExtra("message_paid")!!.toBoolean()){
            txtPaid.text  = "Đã thanh toán"
        }
        else {
            txtPaid.text  = "Chưa thanh toán"
            txtPaid.setTextColor(Color.parseColor("#FFFF000D"))
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("StaticFieldLeak")
    inner class HttpInvoiceDetailAsyncTask : AsyncTask<String, String, String>() {

        private var progressBar = ProgressDialog(this@InvoiceDetailActivity)

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
            var line : String?
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
        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val jsonObj = JSONObject(result)

            // data node is JSON Object
            val jsonData = jsonObj.getJSONObject("data")

            // Lấy nút JSON Object invoice
            val invoice = jsonData.getJSONObject("invoice")

            // Lấy nút JSON Array
            val products = invoice.getJSONArray("products")

            // lặp qua tất cả liên hệ
            for (i in 0 until products.length()) {
                val c = products.getJSONObject(i)

                val productAndQuantity = ProductAndQuantity()
                productAndQuantity.quantity = c.getInt("quantity")

                // Lấy nút JSON Object invoice
                val product = c.getJSONObject("product")
                productAndQuantity.product.name = product.getString("name")
                productAndQuantity.product.img = product.getString("img")
                productAndQuantity.product.price = product.getString("price").toFloat()
                productAndQuantity.product.weight = product.getString("weight").toFloat()

                productList.add(productAndQuantity)
            }

            // Lấy nút JSON Object shipper
            try {
                val shipper = invoice.getJSONObject("shipper")
                txtShipper.text = shipper.getString("name")
            } catch (e: JSONException) {
                e.printStackTrace()
                txtShipper.text = "Chưa có người nhận"
                txtShipper.setTextColor(Color.parseColor("#FFFF000D"))
            }

            // Lấy nút JSON Object customer
            val customer = invoice.getJSONObject("customer")
            txtCustomer.text = " " + customer.getString("name")

            listView.adapter = ProductAndQuantityAdapter(this@InvoiceDetailActivity,productList)

            progressBar.dismiss()
        }
    }
}
