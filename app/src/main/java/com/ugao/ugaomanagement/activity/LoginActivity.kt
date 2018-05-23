package com.ugao.ugaomanagement.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v7.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ugao.ugaomanagement.R

import kotlinx.android.synthetic.main.activity_login.*
import android.content.SharedPreferences
import android.graphics.Color

class LoginActivity : AppCompatActivity() {

    private var mAuthTask: UserLoginTask? = null

    lateinit var sharedPreferences: SharedPreferences
    val myPreference = "myPref"
    val idKey = "idKey"
    val nameKey = "nameKey"
    val emailKey = "emailKey"
    val phoneKey = "phoneKey"
    val storeKey = "storeKey"
    val locationKey = "locationKey"
    val moneyKey = "moneyKey"
    val imgKey = "imgKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_login)

        sign_in_button.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        username.error = null
        password.error = null

        // Lưu trữ giá trị tại thời điểm đăng nhập.
        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Kiểm tra username hợp lệ.
        if (TextUtils.isEmpty(usernameStr)) {
            username.error = getString(R.string.error_required)
            focusView = username
            cancel = true
        }
        else
            if (!isUsernameValid(usernameStr)) {
                username.error = getString(R.string.error_invalid_username)
                focusView = username
                cancel = true
            }

        // Kiểm tra mật khẩu hợp lệ
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(usernameStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    private fun isUsernameValid(usernameStr: String): Boolean {
        return usernameStr.length > 4
    }

    private fun isPasswordValid(passwordStr: String): Boolean {
        return passwordStr.length > 4
    }

    // Hiển thị giao diện người dùng tiến trình và ẩn biểu mẫu đăng nhập.
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            progressBar_form.visibility = if (show) View.VISIBLE else View.GONE
            progressBar_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            progressBar_form.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar_form.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    // kiem tra tai khoan dang nhap
    @SuppressLint("StaticFieldLeak")
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) :
            AsyncTask<Void, Void, Boolean>() {

        private var intent: Intent? = null

        override fun onPreExecute() {
            super.onPreExecute()
            intent = Intent(this@LoginActivity, MainActivity::class.java)
        }

        override fun doInBackground(vararg params: Void): Boolean? {

//            chua co j
            try {
                // Simulate network access.
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                return false
            }

            return true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {

                sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(idKey, "5ac8d41fd9552734e8148e19")
                editor.putString(nameKey, "Blanca Bechtelar")
                editor.putString(emailKey, "Jana75@gmail.com")
                editor.putString(phoneKey, "1234567890")
                editor.putString(locationKey, "7999 Lord Lights")
                editor.putString(storeKey, "Ugao Store 1")
                editor.putString(moneyKey, "100.000Đ")
                editor.putString(imgKey, "https://s3.amazonaws.com/uifaces/faces/twitter/joshuasortino/128.jpg")
                editor.apply()

                startActivity(intent)
                finish()
            }
            else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

}
