package com.jaylangkung.brainnet_staff.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.auth.LoginActivity
import com.jaylangkung.brainnet_staff.databinding.ActivitySettingBinding
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class SettingActivity : AppCompatActivity() {

    private lateinit var settingBinding: ActivitySettingBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(settingBinding.root)
        myPreferences = MySharedPreferences(this@SettingActivity)

        settingBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        settingBinding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this@SettingActivity, EditProfileActivity::class.java))
            finish()
        }

        settingBinding.btnLogout.setOnClickListener {
            val mDialog = MaterialDialog.Builder(this@SettingActivity)
                .setTitle("Logout")
                .setMessage(getString(R.string.confirm_logout))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.no), R.drawable.ic_close
                ) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setNegativeButton(
                    getString(R.string.yes), R.drawable.ic_logout
                ) { dialogInterface, _ ->
                    myPreferences.setValue(Constants.USER, "")
                    myPreferences.setValue(Constants.USER_IDADMIN, "")
                    myPreferences.setValue(Constants.USER_EMAIL, "")
                    myPreferences.setValue(Constants.USER_NAMA, "")
                    myPreferences.setValue(Constants.USER_ALAMAT, "")
                    myPreferences.setValue(Constants.USER_TELP, "")
                    myPreferences.setValue(Constants.FOTO_PATH, "")
                    myPreferences.setValue(Constants.FOTO_PATH, "")
                    myPreferences.setValue(Constants.DEVICE_TOKEN, "")
                    myPreferences.setValue(Constants.TokenAuth, "")

                    startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                    finish()
                    dialogInterface.dismiss()
                }
                .build()
            
            mDialog.show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
        finish()
    }
}