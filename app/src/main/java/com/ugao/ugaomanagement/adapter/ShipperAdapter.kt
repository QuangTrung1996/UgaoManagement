package com.ugao.ugaomanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.model.Shipper
import de.hdodenhof.circleimageview.CircleImageView

class ShipperAdapter : BaseAdapter {

    private var listData: List<Shipper>
    private var context: Context

    constructor(context: Context, listData: List<Shipper>) : super() {
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_shipper, parent, false)
            holder = ViewHolder(view)
            view.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val shipper = listData[position]
        holder.name.text = shipper.name
        holder.email.text = shipper.email
        holder.numberPhone.text = shipper.phone
        Glide.with(context)
                .load(shipper.img)
                .into(holder.profileImage)

        return view
    }

    class ViewHolder (view : View){
        var name    : TextView = view.findViewById(R.id.txt_name)
        var email   : TextView = view.findViewById(R.id.txt_email)
        var numberPhone  : TextView = view.findViewById(R.id.txt_sdt)
        var profileImage: CircleImageView = view.findViewById(R.id.profile_image)
    }
}