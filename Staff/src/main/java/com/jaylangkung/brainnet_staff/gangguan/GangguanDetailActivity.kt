package com.jaylangkung.brainnet_staff.gangguan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityGangguanDetailBinding
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences

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

        gangguanDetailBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        gangguanDetailBinding.btnSubmit.setOnClickListener {
            gangguanDetailBinding.btnSubmit.startAnimation()
            if (validate()) {

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
}