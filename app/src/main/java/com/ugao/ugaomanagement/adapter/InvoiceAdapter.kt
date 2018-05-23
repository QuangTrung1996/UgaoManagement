package com.ugao.ugaomanagement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.widget.TextView
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.model.Invoice
import java.text.SimpleDateFormat
import java.util.*

class InvoiceAdapter : BaseAdapter {

    private var listData: List<Invoice>
    private var context: Context

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
            view = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false)
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

        // doi lai thoi gian
        val df = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.US)
        val df1 = SimpleDateFormat("HH:mm:ss   dd-MM-yyyy", Locale.US)
        df.timeZone = TimeZone.getTimeZone("GMT+0000 (UTC)")
        val date = df.parse(invoice.order_date)
        holder.txt_order_date.text = df1.format(date)

        holder.txt_price.text = invoice.price.toString() + " Đ"

        return view
    }

    class ViewHolder (view : View){
        var id : TextView = view.findViewById(R.id.txt_id)
        var txt_paid: TextView = view.findViewById(R.id.txt_paid)
        var txt_order_date: TextView = view.findViewById(R.id.txt_order_date)
        var txt_price: TextView = view.findViewById(R.id.txt_price)
    }
}