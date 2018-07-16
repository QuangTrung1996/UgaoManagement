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
import com.google.firebase.iid.FirebaseInstanceId
import com.ugao.ugaomanagement.app.Config
import com.ugao.ugaomanagement.app.OwnerUpdate
import com.ugao.ugaomanagement.internet.CheckInternet
import com.ugao.ugaomanagement.internet.CheckInternetInterface
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

@Suppress("UNREACHABLE_CODE")
class LoginActivity : AppCompatActivity(), CheckInternetInterface {

    private val defaultUsernameValue = ""
    private var usernameValue: String? = null

    private val defaultPasswordValue = ""
    private var passwordValue: String? = null

    private var isConnected = true
    private var mAuthTask: UserLoginTask? = null
    private lateinit var sharedPreferences: SharedPreferences
    private val https = "https://gentle-dawn-11577.herokuapp.com/graphql?query={"
    private val query = "{_id, email, name, phone, img, token, store {_id, name, location {address}}}}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_login)

        sign_in_button.setOnClickListener {
            val checkInternet = CheckInternet(this)
            checkInternet.checkConnection(this)
            if (isConnected) {
                attemptLogin()
            }
        }
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
        return "authenticatedOwner(email:%22$usernameStr%22,pass:%22$passwordStr%22)"
    }

    private fun isUsernameValid(email: String): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
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

//            login_form.visibility = if (show) View.GONE else View.VISIBLE
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

                }while (true)
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


                sharedPreferences = getSharedPreferences(Config.myPreference, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(Config.ownerId, owner.getString("_id"))
                editor.putString(Config.ownerEmail, owner.getString("email"))
                editor.putString(Config.ownerName, owner.getString("name"))
                editor.putString(Config.ownerPhone, owner.getString("phone"))
                editor.putString(Config.ownerImg, owner.getString("img"))

                editor.putString(Config.storeName, store.getString("name"))
                editor.putString(Config.storeKey, store.getString("_id"))
                editor.putString(Config.storeLocation, location.getString("address"))
                editor.apply()

                startActivity(intent)

                savePreferences()

                if (FirebaseInstanceId.getInstance().token!! != owner.getString("token")){
                    pushToken()
                }

                finish()
            }
            catch (e: JSONException) {
//                Toast.makeText(this@LoginActivity, "Json parsing error: " + e.message, Toast.LENGTH_LONG).show()
                Toast.makeText(this@LoginActivity, "Không đúng tên hoặc mật khẩu", Toast.LENGTH_LONG).show()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }

    override fun checkInternet(isConnected: Boolean) {
        this.isConnected = isConnected
        showToast(isConnected)
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    private fun savePreferences() {
        val settings = getSharedPreferences(Config.PREF_LOGIN, Context.MODE_PRIVATE)
        val editor = settings.edit()

        // Edit and commit
        usernameValue = username.text.toString()
        passwordValue = password.text.toString()

        editor.putString(Config.PREF_UNAME, usernameValue)
        editor.putString(Config.PREF_PASSWORD, passwordValue)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        loadPreferences()
    }

    private fun loadPreferences() {

        val settings = getSharedPreferences(Config.PREF_LOGIN, Context.MODE_PRIVATE)

        // Get value
        usernameValue = settings.getString(Config.PREF_UNAME, defaultUsernameValue)
        passwordValue = settings.getString(Config.PREF_PASSWORD, defaultPasswordValue)
        username.setText(usernameValue)
        password.setText(passwordValue)
    }

    private fun pushToken() {
        val push = OwnerUpdate()
        push.postToken = FirebaseInstanceId.getInstance().token!!
        push.id = sharedPreferences.getString(Config.ownerId, "")
        push.pushUpdate("token",this@LoginActivity)
    }

    // Showing the status in Toast
    private fun showToast(isConnected : Boolean) {
        if (!isConnected) {
            Toast.makeText(this,"Sorry! Not connected to internet",Toast.LENGTH_LONG).show()
        }
    }
}
