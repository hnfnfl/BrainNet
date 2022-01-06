package com.jaylangkung.brainnet_staff.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityLoginBinding
import com.jaylangkung.brainnet_staff.retrofit.AuthService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.LoginResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var myPreferences: MySharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        myPreferences = MySharedPreferences(this@LoginActivity)

        val deviceID = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)
        loginBinding.btnLogin.setOnClickListener {
            val email = loginBinding.tvValueEmailLogin.text.toString()
            val pass = loginBinding.tvValuePasswordLogin.text.toString()
            if (validate()) {
                loginProcess(email, pass, "hp.$deviceID")
                loginBinding.btnLogin.startAnimation()
            }
        }
    }

    private fun validate(): Boolean {
        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        when {
            loginBinding.tvValueEmailLogin.text.toString() == "" -> {
                loginBinding.tvValueEmailLogin.error = getString(R.string.email_cant_empty)
                loginBinding.tvValueEmailLogin.requestFocus()
                return false
            }
            !loginBinding.tvValueEmailLogin.text.toString().isValidEmail() -> {
                loginBinding.tvValueEmailLogin.error = getString(R.string.email_format_error)
                loginBinding.tvValueEmailLogin.requestFocus()
                return false
            }
            loginBinding.tvValuePasswordLogin.text.toString() == "" -> {
                loginBinding.tvValuePasswordLogin.error = getString(R.string.password_cant_empty)
                loginBinding.tvValuePasswordLogin.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun loginProcess(email: String, password: String, deviceID: String) {
        val service = RetrofitClient().apiRequest().create(AuthService::class.java)
        service.login(email, password, deviceID).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
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
                            loginBinding.btnLogin.endAnimation()
                            Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        }
                        "unauthorized" -> {
                            loginBinding.btnLogin.endAnimation()
                            Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        }
                        "too_many_attempt" -> {
                            loginBinding.btnLogin.endAnimation()
                            Toasty.error(this@LoginActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                        }
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@LoginActivity,
                        "loginProcess | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginBinding.btnLogin.endAnimation()
                ErrorHandler().responseHandler(
                    this@LoginActivity,
                    "loginProcess | onResponse", t.message.toString()
                )
            }
        })
    }

}