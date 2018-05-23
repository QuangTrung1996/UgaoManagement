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
import com.ugao.ugaomanagement.model.ProductAndQuantity
import java.net.URL

@Suppress("DEPRECATION")
class ProductAndQuantityAdapter(private var context: Context, private var listData: List<ProductAndQuantity>) : BaseAdapter() {

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

        val productAndQuantity = listData[position]

        val url = if(productAndQuantity.product.img.startsWith("http://")){
            URL(productAndQuantity.product.img)
        }
        else{
            URL("http://" + productAndQuantity.product.img)
        }

        Glide.with(context)
                .load(url)
                .into(holder.thumbnail)

        holder.txtNameProduct.text = productAndQuantity.product.name
        holder.txtPriceProduct.text     = "Đơn giá : "+productAndQuantity.product.price.toString()
        holder.txtWeightProduct.text    = "Cân nặng: "+productAndQuantity.product.weight.toString() + " kg"
        holder.txtQuantityProduct.text  = "Số lượng: "+productAndQuantity.quantity.toString()

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