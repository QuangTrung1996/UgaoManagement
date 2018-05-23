package com.ugao.ugaomanagement.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ugao.ugaomanagement.R

class NotificationActivity : AppCompatActivity() {

    private lateinit var txtRegId: TextView
    private lateinit var txtMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        txtRegId = findViewById(R.id.txt_reg_id)
        txtMessage = findViewById(R.id.txt_push_message)
    }
}
