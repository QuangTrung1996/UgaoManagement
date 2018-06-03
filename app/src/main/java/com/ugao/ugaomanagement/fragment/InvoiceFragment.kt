package com.ugao.ugaomanagement.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.activity.MainActivity
import com.ugao.ugaomanagement.adapter.InvoiceAdapter
import com.ugao.ugaomanagement.adapter.InvoicePagerAdapter
import com.ugao.ugaomanagement.app.Config
import com.ugao.ugaomanagement.model.Invoice
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class InvoiceFragment : Fragment() {

    lateinit var pager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val roof = inflater.inflate(R.layout.fragment_invoice, container, false)
        pager =  roof.findViewById(R.id.view_pager)
        tabLayout = roof.findViewById(R.id.tab_layout)
        val manager = activity!!.supportFragmentManager
        val adapter = InvoicePagerAdapter(manager)
        pager.adapter = adapter
        tabLayout.setupWithViewPager(pager)
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.setTabsFromPagerAdapter(adapter)

        return roof
    }
}