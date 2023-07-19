package com.jaylangkung.brainnet_staff.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityEditProfileBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.data_class.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var myPreferences: MySharedPreferences
//    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@EditProfileActivity)

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
        val alamat = myPreferences.getValue(Constants.USER_ALAMAT).toString()
        val telp = myPreferences.getValue(Constants.USER_TELP).toString()

        binding.tvValueEmailEdit.setText(email)
        binding.tvValueNameEdit.setText(nama)
        binding.tvValueAddressEdit.setText(alamat)
        binding.tvValuePhoneEdit.setText(telp)

        binding.btnSave.setOnClickListener {
            if (validate()) {
                binding.btnSave.startAnimation()
                val editEmail = binding.tvValueEmailEdit.text.toString()
                val editName = binding.tvValueNameEdit.text.toString()
                val editAddress = binding.tvValueAddressEdit.text.toString()
                val editPhone = binding.tvValuePhoneEdit.text.toString()
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
            binding.tvValueEmailEdit.text.toString() == "" -> {
                binding.tvValueEmailEdit.error = getString(R.string.email_cant_empty)
                binding.tvValueEmailEdit.requestFocus()
                return false
            }
            !binding.tvValueEmailEdit.text.toString().isValidEmail() -> {
                binding.tvValueEmailEdit.error = getString(R.string.email_format_error)
                binding.tvValueEmailEdit.requestFocus()
                return false
            }
            binding.tvValueNameEdit.text.toString() == "" -> {
                binding.tvValueNameEdit.error = "Nama tidak boleh kosong"
                binding.tvValueNameEdit.requestFocus()
                return false
            }
            binding.tvValueAddressEdit.text.toString() == "" -> {
                binding.tvValueAddressEdit.error = "Alamat tidak boleh kosong"
                binding.tvValueAddressEdit.requestFocus()
                return false
            }
            binding.tvValuePhoneEdit.text.toString() == "" -> {
                binding.tvValuePhoneEdit.error = "Nomor HP tidak boleh kosong"
                binding.tvValuePhoneEdit.requestFocus()
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
                    ErrorHandler().responseHandler(
                        this@EditProfileActivity,
                        "editProfile | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.btnSave.endAnimation()
                ErrorHandler().responseHandler(
                    this@EditProfileActivity,
                    "editProfile | onFailure", t.message.toString()
                )
            }
        })
    }
}