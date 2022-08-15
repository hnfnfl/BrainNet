package com.jaylangkung.indirisma.gangguan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityGangguanDetailBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GangguanDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGangguanDetailBinding
    private lateinit var myPreferences: MySharedPreferences

    companion object {
        const val idgangguan = "idgangguan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGangguanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@GangguanDetailActivity)

        val idgangguan = intent.getStringExtra(idgangguan).toString()
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }

            btnSubmit.setOnClickListener {
                val penyelesaian = tvValueSolution.text.toString()
                if (validate()) {
                    btnSubmit.startAnimation()
                    editGangguan(idgangguan, penyelesaian, idadmin, tokenAuth)
                }
            }
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this@GangguanDetailActivity, MainActivity::class.java))
        finish()
    }

    private fun validate(): Boolean {
        if (binding.tvValueSolution.text.toString() == "") {
            binding.tvValueSolution.error = "Penyelesaian tidak boleh kosong"
            binding.tvValueSolution.requestFocus()
            return false
        }
        return true
    }

    private fun editGangguan(idgangguan: String, penyelesaian: String, idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.editGangguan(idgangguan, penyelesaian, idadmin, tokenAuth, "true").enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.btnSubmit.endAnimation()
                        startActivity(Intent(this@GangguanDetailActivity, MainActivity::class.java))
                        Toasty.success(this@GangguanDetailActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                    }
                } else {
                    ErrorHandler().responseHandler(
                        this@GangguanDetailActivity,
                        "editGangguan | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ErrorHandler().responseHandler(
                    this@GangguanDetailActivity,
                    "editGangguan | onFailure", t.message.toString()
                )
            }
        })
    }

}