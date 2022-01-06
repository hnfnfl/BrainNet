package com.jaylangkung.brainnet_staff.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityEditProfileBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editProfileBinding: ActivityEditProfileBinding
    private lateinit var myPreferences: MySharedPreferences
//    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)
        myPreferences = MySharedPreferences(this@EditProfileActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
        val alamat = myPreferences.getValue(Constants.USER_ALAMAT).toString()
        val telp = myPreferences.getValue(Constants.USER_TELP).toString()

        editProfileBinding.tvValueEmailEdit.setText(email)
        editProfileBinding.tvValueNameEdit.setText(nama)
        editProfileBinding.tvValueAddressEdit.setText(alamat)
        editProfileBinding.tvValuePhoneEdit.setText(telp)

        editProfileBinding.btnSave.setOnClickListener {
            if (validate()) {
                editProfileBinding.btnSave.startAnimation()
                val editEmail = editProfileBinding.tvValueEmailEdit.text.toString()
                val editName = editProfileBinding.tvValueNameEdit.text.toString()
                val editAddress = editProfileBinding.tvValueAddressEdit.text.toString()
                val editPhone = editProfileBinding.tvValuePhoneEdit.text.toString()
                editProfile(idadmin, editEmail, editName, editAddress, editPhone, tokenAuth)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
        finish()
    }

    private fun validate(): Boolean {
        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        when {
            editProfileBinding.tvValueEmailEdit.text.toString() == "" -> {
                editProfileBinding.tvValueEmailEdit.error = getString(R.string.email_cant_empty)
                editProfileBinding.tvValueEmailEdit.requestFocus()
                return false
            }
            !editProfileBinding.tvValueEmailEdit.text.toString().isValidEmail() -> {
                editProfileBinding.tvValueEmailEdit.error = getString(R.string.email_format_error)
                editProfileBinding.tvValueEmailEdit.requestFocus()
                return false
            }
            editProfileBinding.tvValueNameEdit.text.toString() == "" -> {
                editProfileBinding.tvValueNameEdit.error = "Nama tidak boleh kosong"
                editProfileBinding.tvValueNameEdit.requestFocus()
                return false
            }
            editProfileBinding.tvValueAddressEdit.text.toString() == "" -> {
                editProfileBinding.tvValueAddressEdit.error = "Alamat tidak boleh kosong"
                editProfileBinding.tvValueAddressEdit.requestFocus()
                return false
            }
            editProfileBinding.tvValuePhoneEdit.text.toString() == "" -> {
                editProfileBinding.tvValuePhoneEdit.error = "Nomor HP tidak boleh kosong"
                editProfileBinding.tvValuePhoneEdit.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun editProfile(idadmin: String, email: String, nama: String, alamat: String, telp: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.editProfile(idadmin, email, nama, alamat, telp, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        myPreferences.setValue(Constants.USER_EMAIL, email)
                        myPreferences.setValue(Constants.USER_NAMA, nama)
                        myPreferences.setValue(Constants.USER_ALAMAT, alamat)
                        myPreferences.setValue(Constants.USER_TELP, telp)
                        Toasty.success(this@EditProfileActivity, "Profil berhasil dirubah", Toasty.LENGTH_LONG).show()
                        startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
                        finish()
                    }
                } else {
                    ErrorHandler().responseHandler(this@EditProfileActivity, response.message())
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                editProfileBinding.btnSave.endAnimation()
                ErrorHandler().responseHandler(this@EditProfileActivity, t.message.toString())
            }
        })
    }
}