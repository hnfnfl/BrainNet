package com.jaylangkung.brainnet_staff.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.LoginResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityLoginBinding
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
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

        val deviceID = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)
        binding.apply {
            btnLogin.setOnClickListener {
                val email = tvValueEmailLogin.text.toString()
                val pass = tvValuePasswordLogin.text.toString()
                if (validate()) {
                    loginProcess(email, pass, "hp.$deviceID")
                    btnLogin.startAnimation()
                }
            }
        }
    }

    private fun validate(): Boolean {
        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        binding.apply {
            return when {
                tvValueEmailLogin.text.toString() == "" -> {
                    tvValueEmailLogin.error = getString(R.string.email_cant_empty)
                    tvValueEmailLogin.requestFocus()
                    false
                }

                !tvValueEmailLogin.text.toString().isValidEmail() -> {
                    tvValueEmailLogin.error = getString(R.string.email_format_error)
                    tvValueEmailLogin.requestFocus()
                    false
                }

                tvValuePasswordLogin.text.toString() == "" -> {
                    tvValuePasswordLogin.error = getString(R.string.password_cant_empty)
                    tvValuePasswordLogin.requestFocus()
                    false
                }

                else -> true
            }
        }
    }

    private fun loginProcess(email: String, password: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.login(email, password, deviceID).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    binding.btnLogin.endAnimation()
                    when (response.body()!!.status) {
                        "success" -> {
                            myPreferences.setValue(Constants.USER, Constants.LOGIN)
                            myPreferences.setValue(Constants.USER_IDADMIN, response.body()!!.data[0].idadmin)
                            myPreferences.setValue(Constants.USER_NAMA, response.body()!!.data[0].nama)
                            myPreferences.setValue(Constants.USER_ALAMAT, response.body()!!.data[0].alamat)
                            myPreferences.setValue(Constants.USER_EMAIL, response.body()!!.data[0].email)
                            myPreferences.setValue(Constants.USER_TELP, response.body()!!.data[0].telp)
                            myPreferences.setValue(Constants.DEVICE_TOKEN, response.body()!!.data[0].device_token)
                            myPreferences.setValue(Constants.FOTO_PATH, response.body()!!.data[0].img)
                            myPreferences.setValue(Constants.TokenAuth, response.body()!!.tokenAuth)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }

                        "not_exist" -> {
                            Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        }

                        "unauthorized" -> {
                            Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        }

                        "too_many_attempt" -> {
                            Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        }
                    }
                } else {
                    binding.btnLogin.endAnimation()
                    ErrorHandler().responseHandler(
                        this@LoginActivity, "loginProcess | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.btnLogin.endAnimation()
                ErrorHandler().responseHandler(
                    this@LoginActivity, "loginProcess | onResponse", t.message.toString()
                )
            }
        })
    }

}