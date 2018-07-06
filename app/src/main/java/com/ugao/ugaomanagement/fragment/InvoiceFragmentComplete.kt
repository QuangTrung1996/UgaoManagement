package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ListView
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.adapter.InvoiceAdapter
import android.os.AsyncTask
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import com.ugao.ugaomanagement.activity.InvoiceDetailActivity
import com.ugao.ugaomanagement.model.Invoice
import java.util.*

// da giao hang

class InvoiceFragmentComplete : Fragment() {

    companion object {
        var invoiceListComplete = ArrayList<Invoice>()
    }

    private lateinit var listView : ListView
    private var httpInvoiceAsyncTask: HttpInvoiceAsyncTask? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val roof = inflater.inflate(R.layout.fragment_invoice_list, container, false)

        listView = roof.findViewById(R.id.lv_invoice)

        httpInvoiceAsyncTask = HttpInvoiceAsyncTask()
        httpInvoiceAsyncTask!!.execute()

        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val intent = Intent(activity, InvoiceDetailActivity::class.java)
            intent.putExtra("message", invoiceListComplete[position].id)
            intent.putExtra("message_order_date", invoiceListComplete[position].orderDate)

            if (invoiceListComplete[position].paid){
                intent.putExtra("message_paid", "true")
            }
            else {
                intent.putExtra("message_paid", "false")
            }

            intent.putExtra("message_price", invoiceListComplete[position].price)

            activity!!.startActivity(intent)
        }

        return roof
    }

    @SuppressLint("StaticFieldLeak")
    inner class HttpInvoiceAsyncTask : AsyncTask<String, String, Boolean>() {

        override fun doInBackground(vararg params: String?): Boolean {
            return true
        }

        //Hàm này được thực hiện khi tiến trình kết thúc
        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            listView.adapter = InvoiceAdapter(activity!!,invoiceListComplete)
        }
    }
}