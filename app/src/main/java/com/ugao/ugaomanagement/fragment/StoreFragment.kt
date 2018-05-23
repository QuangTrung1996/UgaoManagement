package com.ugao.ugaomanagement.fragment

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ugao.ugaomanagement.R
import de.hdodenhof.circleimageview.CircleImageView
import android.R.id.edit
import android.content.Intent
import com.ugao.ugaomanagement.activity.LoginActivity


class StoreFragment: Fragment() {

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

    lateinit var imgCircleImageView : CircleImageView
    lateinit var nameOwner : TextView
    lateinit var emailOwner : TextView
    lateinit var phoneOwner : TextView
    lateinit var nameStore : TextView
    lateinit var locationStore : TextView
    lateinit var money : TextView
    lateinit var btnLogout : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val roof = inflater.inflate(R.layout.fragment_store, container, false)

        init(roof)

        getTextSharedPreferences()

        btnLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent: Intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }

        return roof
    }

    private fun init(view: View) {
        imgCircleImageView  = view.findViewById(R.id.profile_image)

        nameOwner   = view.findViewById(R.id.txt_name_owner)
        emailOwner   = view.findViewById(R.id.txt_email_owner)
        phoneOwner   = view.findViewById(R.id.txt_phone_owner)

        nameStore   = view.findViewById(R.id.txt_name_store)
        locationStore   = view.findViewById(R.id.txt_location_store)

        money   = view.findViewById(R.id.txt_money_store)

        btnLogout = view.findViewById(R.id.btn_logout)
    }

    private fun getTextSharedPreferences() {
        sharedPreferences = activity!!.getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        nameOwner.text = sharedPreferences.getString(nameKey, "")
        emailOwner.text = sharedPreferences.getString(emailKey, "")
        phoneOwner.text = sharedPreferences.getString(phoneKey, "")

        nameStore.text = sharedPreferences.getString(storeKey, "")
        locationStore.text = sharedPreferences.getString(locationKey, "")
        money.text = sharedPreferences.getString(moneyKey, "")

        Glide.with(context)
                .load(sharedPreferences.getString(imgKey, ""))
                .into(imgCircleImageView)
    }
}