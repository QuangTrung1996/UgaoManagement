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
import android.util.Log
import android.widget.Toast
import com.ugao.ugaomanagement.app.Config.myPreference
import com.ugao.ugaomanagement.app.Config.ownerEmail
import com.ugao.ugaomanagement.app.Config.ownerId
import com.ugao.ugaomanagement.app.Config.ownerImg
import com.ugao.ugaomanagement.app.Config.ownerName
import com.ugao.ugaomanagement.app.Config.ownerPhone
import com.ugao.ugaomanagement.app.Config.storeKey
import com.ugao.ugaomanagement.app.Config.storeLocation
import com.ugao.ugaomanagement.app.Config.storeName
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private var mAuthTask: UserLoginTask? = null

    lateinit var sharedPreferences: SharedPreferences

    val https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={"
    val query = "{_id, email, name, phone, img, store {_id, name, location {address}}}}"

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
            mAuthTask = UserLoginTask()
            mAuthTask!!.execute(https + authenticatedOwner(usernameStr,passwordStr) + query)
        }
    }

    private fun authenticatedOwner(usernameStr: String, passwordStr: String): String? {
        return "authenticatedOwner(email:%22" + usernameStr + "%22,pass:%22" +passwordStr + "%22)"
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
    inner class UserLoginTask : AsyncTask<String, String, String>() {

        private var intent: Intent? = null

        override fun onPreExecute() {
            super.onPreExecute()
            intent = Intent(this@LoginActivity, MainActivity::class.java)
        }

        override fun doInBackground(vararg params: String?): String {
            val content = StringBuffer()
            val url = URL(params[0])
            val urlConnection: HttpURLConnection =url.openConnection()as HttpURLConnection
            val inputStreamReader = InputStreamReader(urlConnection.inputStream)
            val bufferReader = BufferedReader(inputStreamReader as Reader?)
            var line : String
            try {
                do {
                    line=bufferReader.readLine()
                    if (line!=null){
                        content.append(line)
                    }

                }while (line != null)
                bufferReader.close()

            }catch (e:Exception){
                Log.d("AAA",e.toString())
            }

            return content.toString()
        }

        //Hàm này được thực hiện khi tiến trình kết thúc
        override fun onPostExecute(result: String?) {
            mAuthTask = null
            showProgress(false)

            try {
                val jsonObj = JSONObject(result)
                val jsonData = jsonObj.getJSONObject("data")
                val owner = jsonData.getJSONObject("authenticatedOwner")
                val store = owner.getJSONObject("store")
                val location = store.getJSONObject("location")


                sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(ownerId, owner.getString("_id"))
                editor.putString(ownerEmail, owner.getString("email"))
                editor.putString(ownerName, owner.getString("name"))
                editor.putString(ownerPhone, owner.getString("phone"))
                editor.putString(ownerImg, owner.getString("img"))

                editor.putString(storeName, store.getString("name"))
                editor.putString(storeKey, store.getString("_id"))
                editor.putString(storeLocation, location.getString("address"))
                editor.apply()

                Toast.makeText(this@LoginActivity, store.getString("_id"), Toast.LENGTH_LONG).show()

                startActivity(intent)
                finish()
            }
            catch (e: JSONException) {
                Toast.makeText(this@LoginActivity,
                        "Json parsing error: " + e.message,
                        Toast.LENGTH_LONG)
                        .show()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

}
