package com.ugao.ugaomanagement.internet

import android.content.Context
import android.net.ConnectivityManager

class CheckInternet constructor(var internet: CheckInternetInterface) {
    var check : CheckInternetInterface = internet
    fun checkConnection(context: Context) {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connMgr.activeNetworkInfo

        if (activeNetworkInfo != null) {
            //Toast.makeText(context, activeNetworkInfo.typeName, Toast.LENGTH_SHORT).show()
            // niếu có internet gọi funtion kientrainternet trong interface truyền vào biến true
            check.checkInternet(true)

            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                // check.checkInternet(true)
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                // check.checkInternet(true)
            }
        }
        else {
            // niếu ko có internet gọi funtion kientrainternet trong interface truyền vào biến false
            check.checkInternet(false)
        }
    }
}