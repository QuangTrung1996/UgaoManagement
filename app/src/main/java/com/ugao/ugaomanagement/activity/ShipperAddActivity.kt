package com.ugao.ugaomanagement.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.EditText
import com.ugao.ugaomanagement.R
import android.widget.Button
import java.util.regex.Pattern
import android.text.TextUtils
import android.view.View
import com.ugao.ugaomanagement.app.ShipperAdd
import com.ugao.ugaomanagement.model.Shipper

class ShipperAddActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    private lateinit var txtName    : EditText
    private lateinit var txtPhone   : EditText
    private lateinit var txtAddress : EditText
    private lateinit var txtEmail   : EditText
    private lateinit var txtPass1   : EditText
    private lateinit var txtPass2   : EditText

    private lateinit var btnAdd     : Button
    private lateinit var btnCancel  : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipper_add)

        toolbar = findViewById(R.id.toolbar_elevated)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            //Hiện nút back
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        init()

        btnAdd.setOnClickListener { submitForm() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun init() {
        txtName     = findViewById(R.id.text_input_edit_name)
        txtPhone    = findViewById(R.id.input_phone)
        txtAddress  = findViewById(R.id.input_address)
        txtEmail    = findViewById(R.id.input_email)
        txtPass1    = findViewById(R.id.input_password_1)
        txtPass2    = findViewById(R.id.input_password_2)

        btnAdd      = findViewById(R.id.btn_shipper_add)
        btnCancel   = findViewById(R.id.btn_shipper_cancel)
    }

    private fun submitForm() {

        val name   = txtName.text.toString()
        val phone  = txtPhone.text.toString()
        val address= txtAddress.text.toString()
        val email  = txtEmail.text.toString()
        val pass1  = txtPass1.text.toString()
        val pass2  = txtPass2.text.toString()

        var cancel = false
        var focusView: View? = null

        // Kiểm tra ho va ten
        if (TextUtils.isEmpty(name)) {
            txtName.error = getString(R.string.error_required)
            focusView = txtName
            cancel = true
        }

        // Kiểm tra so dien thoai
        if (TextUtils.isEmpty(phone)) {
            txtPhone.error = getString(R.string.error_required)
            focusView = txtPhone
            cancel = true
        }

        // Kiểm tra dia chi
        if (TextUtils.isEmpty(address)) {
            txtAddress.error = getString(R.string.error_required)
            focusView = txtAddress
            cancel = true
        }

        // Kiểm tra email
        if (TextUtils.isEmpty(email)) {
            txtEmail.error = getString(R.string.error_required)
            focusView = txtEmail
            cancel = true
        }
        else if (!isEmailValid(email)) {
                txtEmail.error = getString(R.string.error_invalid_username)
                focusView = txtEmail
                cancel = true
        }

        // Kiểm tra pass
        if (TextUtils.isEmpty(pass1)) {
            txtPass1.error = getString(R.string.error_invalid)
            focusView = txtPass1
            cancel = true
        }
        else if (!isPasswordValid(pass1)) {
            txtPass1.error = getString(R.string.error_invalid_password)
            focusView = txtPass1
            cancel = true
        }
        else if (TextUtils.isEmpty(pass2)) {
            txtPass2.error = getString(R.string.error_invalid)
            focusView = txtPass2
            cancel = true
        }
        else if (!isPasswordValid(pass2)) {
            txtPass2.error = getString(R.string.error_invalid_password)
            focusView = txtPass2
            cancel = true
        }
        else if (pass1 != pass2) {
            txtPass2.error = getString(R.string.error_invalid_pass)
            focusView = txtPass2
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        }
        else {
            val shipper= Shipper()

            shipper.name    = name
            shipper.phone   = phone
            shipper.address = address
            shipper.email   = email
            shipper.pass    = pass1

            val push = ShipperAdd()
            push.postAddShipper(shipper, this@ShipperAddActivity)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isPasswordValid(passwordStr: String): Boolean {
        return passwordStr.length > 4
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
