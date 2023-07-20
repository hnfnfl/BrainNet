package com.jaylangkung.brainnet_staff.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.DefaultResponse
import com.jaylangkung.brainnet_staff.databinding.ActivityEditProfileBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@EditProfileActivity, SettingActivity::class.java))
                finish()
            }
        })

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val nama = myPreferences.getValue(Constants.USER_NAMA).toString()
        val email = myPreferences.getValue(Constants.USER_EMAIL).toString()
        val alamat = myPreferences.getValue(Constants.USER_ALAMAT).toString()
        val telp = myPreferences.getValue(Constants.USER_TELP).toString()

        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            tvValueEmailEdit.setText(email)
            tvValueNameEdit.setText(nama)
            tvValueAddressEdit.setText(alamat)
            tvValuePhoneEdit.setText(telp)



            btnSave.setOnClickListener {
                if (validate()) {
                    btnSave.startAnimation()
                    val editEmail = tvValueEmailEdit.text.toString()
                    val editName = tvValueNameEdit.text.toString()
                    val editAddress = tvValueAddressEdit.text.toString()
                    val editPhone = tvValuePhoneEdit.text.toString()
                    editProfile(idadmin, editEmail, editName, editAddress, editPhone, tokenAuth)
                }
            }
        }
    }

    private fun validate(): Boolean {
        fun String.isValidEmail() = isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
        binding.apply {
            return when {
                tvValueEmailEdit.text.toString() == "" -> {
                    tvValueEmailEdit.error = getString(R.string.email_cant_empty)
                    tvValueEmailEdit.requestFocus()
                    false
                }

                !tvValueEmailEdit.text.toString().isValidEmail() -> {
                    tvValueEmailEdit.error = getString(R.string.email_format_error)
                    tvValueEmailEdit.requestFocus()
                    false
                }

                tvValueNameEdit.text.toString() == "" -> {
                    tvValueNameEdit.error = "Nama tidak boleh kosong"
                    tvValueNameEdit.requestFocus()
                    false
                }

                tvValueAddressEdit.text.toString() == "" -> {
                    tvValueAddressEdit.error = "Alamat tidak boleh kosong"
                    tvValueAddressEdit.requestFocus()
                    false
                }

                tvValuePhoneEdit.text.toString() == "" -> {
                    tvValuePhoneEdit.error = "Nomor HP tidak boleh kosong"
                    tvValuePhoneEdit.requestFocus()
                    false
                }

                else -> true
            }
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
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@EditProfileActivity, "editProfile | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.btnSave.endAnimation()
                ErrorHandler().responseHandler(
                    this@EditProfileActivity, "editProfile | onFailure", t.message.toString()
                )
            }
        })
    }
}