package com.ugao.ugaomanagement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.activity.InvoiceDetailActivity
import com.ugao.ugaomanagement.app.Config.AUTH_KEY
import com.ugao.ugaomanagement.app.Config.myPreference
import com.ugao.ugaomanagement.app.Config.storeName
import com.ugao.ugaomanagement.model.Invoice
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class InvoicePaperAdapterNew : BaseAdapter {

    private var listData: List<Invoice>
    private var context: Context

    // doi lai thoi gian
    val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.US)
    val df1 = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)

    constructor(context: Context, listData: List<Invoice>) : super() {
        this.listData = listData
        this.context = context
    }

    override fun getItem(position: Int): Any {
        return listData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listData.size
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_invoice_new, parent, false)
            holder = ViewHolder(view)
            view.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val invoice = listData[position]

        holder.id.text = invoice.id

        if (invoice.paid){
            holder.txt_paid.text = "ĐÃ THANH TOÁN"
            holder.txt_paid.setTextColor(Color.parseColor("#FF2873E4"))
        }
        else {
            holder.txt_paid.text = "CHƯA THANH TOÁN"
            holder.txt_paid.setTextColor(Color.parseColor("#FFFF000D"))
        }

        df.timeZone = TimeZone.getTimeZone("GMT+0000 (UTC)")
        val date = df.parse(invoice.order_date)

        holder.txt_order_date.text = testTime(date) //df1.format(date)

        holder.txt_price.text = invoice.price + ".000 Đ"

        holder.detailInvoice.setOnClickListener {
            val intent = Intent(context, InvoiceDetailActivity::class.java)
            intent.putExtra("message", invoice.id)
            intent.putExtra("message_order_date", invoice.order_date)
            if (invoice.paid){
                intent.putExtra("message_paid", "true")
            }
            else {
                intent.putExtra("message_paid", "false")
            }
            intent.putExtra("message_price", invoice.price)
            context.startActivity(intent)
        }

        holder.messageShipper.setOnClickListener {
            FirebaseMessaging.getInstance().subscribeToTopic("news")
            sendWithOtherThread("topic")
        }

        return view
    }

    class ViewHolder (view : View){
        var id : TextView = view.findViewById(R.id.txt_id)
        var txt_paid: TextView = view.findViewById(R.id.txt_paid)
        var txt_order_date: TextView = view.findViewById(R.id.txt_order_date)
        var txt_price: TextView = view.findViewById(R.id.txt_price)

        var messageShipper: Button = view.findViewById(R.id.btn_message_shipper)
        var detailInvoice : Button = view.findViewById(R.id.btn_detail_invoice)
    }

    private fun testTime(date : Date) : String{
        val curDate = Date()
        val number = curDate.time.toInt() - date.time.toInt()

        val s = 1000
        val m = 60 * s
        val h = 60 * m
        val d = 24 * h

        return when {
            number / d in 1..7 -> {
                " " + number / d + " ngày trước"
            }
            number / h >= 1 -> {
                " " +number / d + " giờ trước"
            }
            number / m >= 1 -> {
                " " +number / d + " phút trước"
            }
            number / s >= 1 -> {
                " " +number / d + " giây trước"
            }
            else -> df1.format(date)
        }
    }

    private fun sendWithOtherThread(type: String) {
        Thread(Runnable { pushNotification(type) }).start()
    }

    lateinit var sharedPreferences: SharedPreferences


    private fun pushNotification(type: String) {

        sharedPreferences = context.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        val jPayload = JSONObject()
        val jNotification = JSONObject()
        val jData = JSONObject()
        try {
            jNotification.put("title", "Giao gạo")
            jNotification.put("body", sharedPreferences.getString(storeName, ""))
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
            conn.setRequestProperty("Authorization", AUTH_KEY)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            // Send FCM message content.
            val outputStream = conn.outputStream
            outputStream.write(jPayload.toString().toByteArray())

            // Read FCM response.
            val inputStream = conn.inputStream
            val resp = convertStreamToString(inputStream)

            val h = Handler(Looper.getMainLooper())
//            h.post({ mTextView.text = resp })

            h.post({ Toast.makeText(context,resp,Toast.LENGTH_SHORT).show() })

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