package com.jaylangkung.brainnet_staff.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.BuildConfig
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.auth.LoginActivity
import com.jaylangkung.brainnet_staff.databinding.ActivitySettingBinding
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : AppCompatActivity() {

    private lateinit var settingBinding: ActivitySettingBinding
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(settingBinding.root)
        myPreferences = MySharedPreferences(this@SettingActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()

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
                    logout(idadmin)
                    startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                    finish()
                    dialogInterface.dismiss()
                }
                .build()
            
            mDialog.show()
        }

        settingBinding.appVersion.text = BuildConfig.VERSION_NAME
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
        finish()
    }

    private fun logout(idadmin: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.logout(idadmin).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Log.d("sukses ", "berhasil logout")
                    }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toasty.error(this@SettingActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }
}