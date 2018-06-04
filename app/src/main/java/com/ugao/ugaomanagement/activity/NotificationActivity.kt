package com.ugao.ugaomanagement.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ugao.ugaomanagement.R
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class NotificationActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        toolbar = findViewById(R.id.toolbar_elevated)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val txtTitle : TextView = findViewById(R.id.txt_title)
        val txtBody : TextView = findViewById(R.id.txt_body)

        //get notification data info
        val bundle = intent.extras
        if (bundle != null) {
            //bundle must contain all info sent in "data" field of the notification
            txtTitle.text = bundle.getString("title")
            txtBody.text = bundle.getString("body")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
