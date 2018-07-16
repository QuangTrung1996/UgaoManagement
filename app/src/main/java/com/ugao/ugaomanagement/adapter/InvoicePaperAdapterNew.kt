package com.ugao.ugaomanagement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
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

class InvoicePaperAdapterNew(private var context: Context, private var listData: List<Invoice>) : BaseAdapter() {

    var id : String = ""

    // doi lai thoi gian
    private val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.US)
    private val df1 = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)

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
            holder.txtPaid.text = "ĐÃ THANH TOÁN"
            holder.txtPaid.setTextColor(Color.parseColor("#FF2873E4"))
        }
        else {
            holder.txtPaid.text = "CHƯA THANH TOÁN"
            holder.txtPaid.setTextColor(Color.parseColor("#FFFF000D"))
        }

        df.timeZone = TimeZone.getTimeZone("GMT+0000 (UTC)")
        val date = df.parse(invoice.orderDate)

        holder.txtOrderDate.text = testTime(date) //df1.format(date)

        holder.txtPrice.text = (invoice.price.toFloat() * 1000).toInt().toString() + " Đ"

        holder.detailInvoice.setOnClickListener {
            val intent = Intent(context, InvoiceDetailActivity::class.java)
            intent.putExtra("message", invoice.id)
            intent.putExtra("message_order_date", invoice.orderDate)
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

            // lay ma hoa don
            id = invoice.id
        }

        return view
    }

    class ViewHolder (view : View){
        var id : TextView = view.findViewById(R.id.txt_id)
        var txtPaid: TextView = view.findViewById(R.id.txt_paid)
        var txtOrderDate: TextView = view.findViewById(R.id.txt_order_date)
        var txtPrice: TextView = view.findViewById(R.id.txt_price)

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
//            number / d in 22..27 -> " 3 tuần trước"
//            number / d in 15..21 -> " 2 tuần trước"
//            number / d in 8..14 -> " 1 tuần trước"
            number / d in 1..7  -> " " + number / d + " ngày trước"
            number / h in 1..24 -> " " + number / h + " giờ trước"
            number / m in 1..60 -> " " + number / m + " phút trước"
            number / s in 1..60 -> " " + number / s + " giây trước"
            else -> " " + df1.format(date)
        }
    }

    private fun sendWithOtherThread(type: String) {
        Thread(Runnable { pushNotification(type) }).start()
    }

    private lateinit var sharedPreferences: SharedPreferences

    private fun pushNotification(type: String) {

        sharedPreferences = context.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        val jPayload = JSONObject()
        val jNotification = JSONObject()
        val jData = JSONObject()
        try {
            jNotification.put("title", id)
            jNotification.put("body", "Đơn hàng mới")
            jNotification.put("sound", "default")
            jNotification.put("badge", "1")
//            jNotification.put("click_action", "OPEN_ACTIVITY_1")
            jNotification.put("icon", "ic_notification")

            jData.put("title", id)
            jData.put("body", "Đơn hàng mới")

            when (type) {
//                "tokens" -> {
//                    val ja = JSONArray()
//                    ja.put("e69hnI7EZDQ:APA91bHPjmWNzF5P0trwlO328esm34SSKedBmtK7VznaFLk-kg9GT2aVkrCcFRZz7GE3vHuk-4TF0xP_8tnW8GojcQ-tnAz7mPXYhI31_7XCR2TILKKx6pr7JbWX3VkTetylj3R4BIzA")
//                    ja.put(FirebaseInstanceId.getInstance().token)
//                    jPayload.put("registration_ids", ja)
//                }
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
            val h = Handler(Looper.getMainLooper())
            val resp = convertStreamToString(inputStream)

//            h.post({ Toast.makeText(context, "Đã gửi!!!\n$resp",Toast.LENGTH_SHORT).show() })
            h.post { Toast.makeText(context, "Đã gửi!!!",Toast.LENGTH_SHORT).show() }


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