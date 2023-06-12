package com.jaylangkung.korem.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.UserResponse
import com.jaylangkung.korem.databinding.ActivityLoginBinding
import com.jaylangkung.korem.retrofit.AuthService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var myPreferences: MySharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@LoginActivity)

        val deviceID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
        binding.btnLogin.setOnClickListener {
            val email = binding.tvValueNrpLogin.text.toString()
            val pass = binding.tvValuePasswordLogin.text.toString()
            if (validate()) {
                loginProcess(email, pass, "hp.$deviceID")
                binding.btnLogin.startAnimation()
            }
        }
    }

    private fun validate(): Boolean {
        return when {
            binding.tvValueNrpLogin.text.toString() == "" -> {
                binding.tvValueNrpLogin.error = getString(R.string.nrp_cant_empty)
                binding.tvValueNrpLogin.requestFocus()
                false
            }
            binding.tvValuePasswordLogin.text.toString() == "" -> {
                binding.tvValuePasswordLogin.error = getString(R.string.password_cant_empty)
                binding.tvValuePasswordLogin.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun loginProcess(email: String, password: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.login(email, password, deviceID).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                when (response.body()!!.status) {
                    "success" -> {
                        val dataUser = response.body()!!.data[0]
                        myPreferences.setValue(Constants.USER, Constants.LOGIN)
                        myPreferences.setValue(Constants.USER_IDAKTIVASI, dataUser.iduser_aktivasi)
                        myPreferences.setValue(Constants.USER_NAMA, dataUser.nama)
                        myPreferences.setValue(Constants.USERNAME, dataUser.username)
                        myPreferences.setValue(Constants.USER_TELP, dataUser.notelp)
                        myPreferences.setValue(Constants.FOTO_PATH, dataUser.img)
                        myPreferences.setValue(Constants.USER_PANGKAT, dataUser.pangkat)
                        myPreferences.setValue(Constants.USER_PANGKATJABATAN, dataUser.pangkat_jabatan)
                        myPreferences.setValue(Constants.USER_JABATAN, dataUser.jabatan)
                        myPreferences.setValue(Constants.USER_CORPS, dataUser.corps)
                        myPreferences.setValue(Constants.USER_TGLLAHIR, dataUser.tanggal_lahir)
                        myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)

                        if (dataUser.akses_surat) {
                            myPreferences.clear()
                            binding.btnLogin.endAnimation()
                            Toasty.warning(this@LoginActivity, "Silahkan gunakan aplikasi e-office", Toasty.LENGTH_LONG).show()
                        } else {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }

                    }
                    "not_exist" -> {
                        binding.btnLogin.endAnimation()
                        Toasty.warning(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                    }
                    "forbidden" -> {
                        binding.btnLogin.endAnimation()
                        Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                    }
                    else -> {
                        binding.btnLogin.endAnimation()
                        Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        ErrorHandler().responseHandler(
                            this@LoginActivity,
                            "loginProcess | onResponse", response.body()!!.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                binding.btnLogin.endAnimation()
                ErrorHandler().responseHandler(
                    this@LoginActivity,
                    "loginProcess | onFailure", t.message.toString()
                )
            }
        })
    }
}