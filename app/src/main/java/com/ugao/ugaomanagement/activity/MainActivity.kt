package com.ugao.ugaomanagement.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.ugao.ugaomanagement.R
import com.ugao.ugaomanagement.fragment.*

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    lateinit var navigation : BottomNavigationView

    var toolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar_elevated)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }

        openInvoice()

        navigation = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun openInvoice() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, InvoiceFragment()).commit()
        supportActionBar!!.setTitle(R.string.title_invoice)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_invoice -> {
                openInvoice()
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_shipper -> {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, ShipperFragment()).commit()

                supportActionBar!!.setTitle(R.string.title_shipper)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_product -> {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, StorageFragment()).commit()

                supportActionBar!!.setTitle(R.string.title_product)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_person -> {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, StoreFragment()).commit()

                supportActionBar!!.setTitle(R.string.title_person)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
