@file:Suppress("DEPRECATION")

package com.ugao.ugaomanagement.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ugao.ugaomanagement.R
import de.hdodenhof.circleimageview.CircleImageView
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.widget.*
import com.ugao.ugaomanagement.activity.LoginActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ugao.ugaomanagement.app.Config
import com.ugao.ugaomanagement.internet.CheckInternet
import com.ugao.ugaomanagement.internet.CheckInternetInterface
import android.widget.Toast
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.app.ProgressDialog
import android.net.Uri
import com.ugao.ugaomanagement.app.OwnerUpdate
import java.io.FileNotFoundException


class StoreFragment: Fragment(), CheckInternetInterface {

    private var isConnected = true

    //Firebase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var filePath: Uri? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferencesLogin: SharedPreferences

    private lateinit var imgCircleImageView : CircleImageView
    private lateinit var nameOwner : TextView
    private lateinit var emailOwner : TextView
    private lateinit var phoneOwner : TextView
    private lateinit var nameStore : TextView
    private lateinit var locationStore : TextView

    private lateinit var changeInformation : LinearLayout
    private lateinit var changeImage : LinearLayout
    private lateinit var changePassword : LinearLayout
    //    private lateinit var money : TextView
    private lateinit var btnLogout : Button

    //  post thong tin thay doi
    private val push = OwnerUpdate()
    private lateinit var postNameOwner : String
    private lateinit var postPhone : String
    private lateinit var postPass : String
    private lateinit var postImage : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val roof = inflater.inflate(R.layout.fragment_store, container, false)

        init(roof)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        sharedPreferences = activity!!.getSharedPreferences(Config.myPreference, Context.MODE_PRIVATE)
        preferencesLogin = activity!!.getSharedPreferences(Config.PREF_LOGIN, Context.MODE_PRIVATE)
        getTextSharedPreferences()

        push.id = sharedPreferences.getString(Config.ownerId, "")

        val checkInternet = CheckInternet(this)
        checkInternet.checkConnection(activity!!)
        if (isConnected) {

            btnLogout.setOnClickListener {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }

            changeInformation.setOnClickListener {
                updateInformation()
            }

            changeImage.setOnClickListener {
                getImageFromAlbum()
            }

            changePassword.setOnClickListener {
                updatePassword()
            }
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

//        money   = view.findViewById(R.id.txt_money_store)

        changeInformation = view.findViewById(R.id.ll_change_information)
        changeImage = view.findViewById(R.id.ll_change_image)
        changePassword = view.findViewById(R.id.ll_change_password)

        btnLogout = view.findViewById(R.id.btn_logout)
    }

    private fun getTextSharedPreferences() {

        nameOwner.text = sharedPreferences.getString(Config.ownerName, "")
        emailOwner.text = sharedPreferences.getString(Config.ownerEmail, "")
        phoneOwner.text = sharedPreferences.getString(Config.ownerPhone, "")

        nameStore.text = sharedPreferences.getString(Config.storeName, "")
        locationStore.text = sharedPreferences.getString(Config.storeLocation, "")
//        money.text = "ssss"

        Glide.with(context)
                .load(sharedPreferences.getString(Config.ownerImg, ""))
                .into(imgCircleImageView)
    }

    private fun updatePassword() {
        val li = LayoutInflater.from(context)
        val information = li.inflate(R.layout.dialog_update_password, null)
        val alertDialogBuilder = AlertDialog.Builder(context!!)
        alertDialogBuilder.setTitle("Đổi mật khẩu")
        alertDialogBuilder.setView(information)

        val passOld : EditText = information.findViewById(R.id.password_old)
        val passNew1 : EditText = information.findViewById(R.id.password_new_1)
        val passNew2 : EditText = information.findViewById(R.id.password_new_2)

        alertDialogBuilder.setNegativeButton("Hủy") { dialog, _ ->
            //if user select "Hủy", just cancel this dialog and continue with app
            dialog.cancel()
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            //if user pressed "OK", then he is allowed to exit from application

            if (!passOld.text.isEmpty() && !passNew1.text.isEmpty() && !passNew2.text.isEmpty()){
                if (isPasswordValid(passOld.text.toString())){
                    if(passOld.text.toString() == preferencesLogin.getString(Config.PREF_PASSWORD, "")){
                        if(passNew1.text.toString() == passNew2.text.toString()){

                            postPass = passNew1.text.toString()

//                            pushUpdate("pass")
                            push.postPass = postPass
                            push.pushUpdate("pass")

                            val editor = preferencesLogin.edit()
                            editor.putString(Config.PREF_PASSWORD, passNew1.text.toString())
                            editor.apply()
                        } else{
                            showToast("Mật khẩu mới không giống.")
                        }
                    } else{
                        showToast("Mật khẩu cũ không đúng.")
                    }
                }
            } else{
                showToast("Mật khẩu còn thiếu.")
            }
        }

        val dialog = alertDialogBuilder.create()
        dialog.window.attributes.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun updateInformation() {
        val li = LayoutInflater.from(context)
        val information = li.inflate(R.layout.dialog_update_information, null)
        val alertDialogBuilder = AlertDialog.Builder(context!!)
        alertDialogBuilder.setTitle("Chỉnh sửa thông tin")
        alertDialogBuilder.setView(information)

        val editOwnerName : EditText = information.findViewById(R.id.edit_owner_name)
        val editPhone : EditText = information.findViewById(R.id.edit_phone)
        val editStoreName : EditText = information.findViewById(R.id.edit_store_name)
        val editStoreAddress : EditText = information.findViewById(R.id.edit_store_address)

        editOwnerName.setText(sharedPreferences.getString(Config.ownerName, ""))
        editPhone.setText(sharedPreferences.getString(Config.ownerPhone, ""))
        editStoreName.setText(sharedPreferences.getString(Config.storeName, ""))
        editStoreAddress.setText(sharedPreferences.getString(Config.storeLocation, ""))

        alertDialogBuilder.setNegativeButton("Hủy") { dialog, _ ->
            //if user select "Hủy", just cancel this dialog and continue with app
            dialog.cancel()
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            //if user pressed "OK", then he is allowed to exit from application

            postNameOwner = editOwnerName.text.toString()
            postPhone = editPhone.text.toString()

//            pushUpdate("owner")
            push.postNameOwner = postNameOwner
            push.postPhone = postPhone
            push.pushUpdate("owner")

            val editor = sharedPreferences.edit()
            editor.putString(Config.ownerName, editOwnerName.text.toString())
            editor.putString(Config.ownerPhone, editPhone.text.toString())
//                      editor.putString(Config.storeName, editStoreName.text.toString())
//                      editor.putString(Config.storeLocation, editStoreAddress.text.toString())
            editor.apply()
//
            getTextSharedPreferences()
        }

        val dialog = alertDialogBuilder.create()
        dialog.window.attributes.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun getImageFromAlbum() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            try {
                filePath = data.data
                val imageStream = activity!!.contentResolver.openInputStream(filePath)
                val selectedImage = BitmapFactory.decodeStream(imageStream)

                showImage(selectedImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                showToast("Đã xảy ra sự cố")
            }
        }
        else {
            showToast("Bạn chưa chọn hình ảnh")
        }
    }

    private fun showImage(selectedImage: Bitmap) {
        val li = LayoutInflater.from(context)
        val information = li.inflate(R.layout.dialog_update_image, null)
        val alertDialogBuilder = AlertDialog.Builder(context!!)
        alertDialogBuilder.setTitle("Chỉnh sửa ảnh")
        alertDialogBuilder.setView(information)

        val imgUpdate : ImageView = information.findViewById(R.id.img_update)
        imgUpdate.setImageBitmap(selectedImage)

        alertDialogBuilder.setNegativeButton("Hủy") { dialog, _ ->
            //if user select "Hủy", just cancel this dialog and continue with app
            dialog.cancel()
        }

        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            //if user pressed "OK", then he is allowed to exit from application
            uploadImage()
        }

        val dialog = alertDialogBuilder.create()
        dialog.window.attributes.windowAnimations = R.style.DialogTheme
        dialog.show()
    }

    private fun uploadImage(){
        if(filePath != null){
            val dialog = ProgressDialog(activity)
            dialog.setTitle("Uploading...")
            dialog.show()

            val id = sharedPreferences.getString(Config.ownerId, "")
            val nameFileImage = "image_$id.jpg"

            val ref: StorageReference = storageReference.child(id).child(nameFileImage)
            ref.putFile(filePath!!)
                    .addOnSuccessListener{taskSnapshot ->
                        postImage = taskSnapshot.downloadUrl.toString()

//                        pushUpdate("image")
                        push.postImage = postImage
                        push.pushUpdate("image")

                        dialog.dismiss()
                        showToast("Thành công.")

                        val editor = sharedPreferences.edit()
                        editor.putString(Config.ownerImg, postImage)
                        editor.apply()

                        Glide.with(context)
                                .load(postImage)
                                .into(imgCircleImageView)
                    }
                    .addOnFailureListener {
                        dialog.dismiss()
                        showToast("Thất bại.")
                    }
        }
    }

    private fun isPasswordValid(passwordStr: String): Boolean {
        return passwordStr.length > 4
    }

    override fun checkInternet(isConnected: Boolean) {
        this.isConnected = isConnected
        showToast(isConnected)
    }

    // Showing the status in Toast
    private fun showToast(str : String) {
        Toast.makeText(activity, str, Toast.LENGTH_LONG).show()
    }

    private fun showToast(isConnected : Boolean) {
        if (!isConnected) {
            Toast.makeText(activity,"Sorry! Not connected to internet",Toast.LENGTH_LONG).show()
        }
    }
}