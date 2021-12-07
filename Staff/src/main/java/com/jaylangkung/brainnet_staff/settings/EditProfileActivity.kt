package com.jaylangkung.brainnet_staff.settings

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityEditProfileBinding
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editProfileBinding: ActivityEditProfileBinding
    private lateinit var myPreferences: MySharedPreferences
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)
        myPreferences = MySharedPreferences(this@EditProfileActivity)


    }
}