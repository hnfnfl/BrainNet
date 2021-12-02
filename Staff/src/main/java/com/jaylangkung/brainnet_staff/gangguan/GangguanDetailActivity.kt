package com.jaylangkung.brainnet_staff.gangguan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityGangguanDetailBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GangguanDetailActivity : AppCompatActivity() {

    private lateinit var gangguanDetailBinding: ActivityGangguanDetailBinding
    private lateinit var myPreferences: MySharedPreferences

    companion object {
        const val idgangguan = "idgangguan"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gangguanDetailBinding = ActivityGangguanDetailBinding.inflate(layoutInflater)
        setContentView(gangguanDetailBinding.root)
        myPreferences = MySharedPreferences(this@GangguanDetailActivity)

        val idgangguan = intent.getStringExtra(idgangguan).toString()
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        gangguanDetailBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        gangguanDetailBinding.btnSubmit.setOnClickListener {
            val penyelesaian = gangguanDetailBinding.tvValueSolution.text.toString()
            if (validate()) {
                gangguanDetailBinding.btnSubmit.startAnimation()
                editGangguan(idgangguan, penyelesaian, idadmin, tokenAuth)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@GangguanDetailActivity, MainActivity::class.java))
        finish()
    }

    private fun validate(): Boolean {
        if (gangguanDetailBinding.tvValueSolution.text.toString() == "") {
            gangguanDetailBinding.tvValueSolution.error = "Penyelesaian tidak boleh kosong"
            gangguanDetailBinding.tvValueSolution.requestFocus()
            return false
        }
        return true
    }

    private fun editGangguan(idgangguan: String, penyelesaian: String, idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.editGangguan(idgangguan, penyelesaian, idadmin, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        gangguanDetailBinding.btnSubmit.endAnimation()
                        startActivity(Intent(this@GangguanDetailActivity, MainActivity::class.java))
                        Toasty.success(this@GangguanDetailActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toasty.error(this@GangguanDetailActivity, R.string.try_again, Toasty.LENGTH_LONG).show()
            }
        })
    }
}