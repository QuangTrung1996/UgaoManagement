package com.ugao.ugaomanagement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.model.Storage
import java.net.URL

class StorageAdapter : BaseAdapter {

    private var listData: List<Storage>
    private var context: Context

    constructor(context: Context, listData: List<Storage>) : super() {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
            holder = ViewHolder(view)
            view.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val product = listData[position]

        val url = if(product.product.img.startsWith("http://")){
            URL(product.product.img)
        }
        else{
            URL("http://" + product.product.img)
        }

        Glide.with(context)
                .load(url)
                .into(holder.thumbnail)

        holder.txtNameProduct.text = product.product.name
        holder.txtPriceProduct.text = "Đơn giá : " + (product.product.price * 1000).toInt().toString() + " Đ"
        holder.txtWeightProduct.text = "Cân nặng: " + product.product.weight.toString() + " kg"
        holder.txtQuantityProduct.text = "Số lượng: " + product.amount.toInt().toString()

        return view
    }

    class ViewHolder (view : View){
        var thumbnail: ImageView = view.findViewById(R.id.img_thumbnail)
        var txtNameProduct: TextView = view.findViewById(R.id.txt_name_product)
        var txtPriceProduct: TextView = view.findViewById(R.id.txt_price_product)
        var txtWeightProduct: TextView = view.findViewById(R.id.txt_weight_product)
        var txtQuantityProduct: TextView = view.findViewById(R.id.txt_quantity_product)
    }
}