package com.jaylangkung.brainnet_staff.hal_baik

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityHalBaikBinding
import com.jaylangkung.brainnet_staff.databinding.BottomSheetHalBaikBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.HalBaikResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.ErrorHandler
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HalBaikActivity : AppCompatActivity() {

    private lateinit var halBaikBinding: ActivityHalBaikBinding
    private lateinit var addHalBaikBinding: BottomSheetHalBaikBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var halBaikAdapter: HalBaikAdapter
    private var listHalBaik: ArrayList<HalBaikEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        halBaikBinding = ActivityHalBaikBinding.inflate(layoutInflater)
        setContentView(halBaikBinding.root)
        myPreferences = MySharedPreferences(this@HalBaikActivity)
        halBaikAdapter = HalBaikAdapter()

        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getHalBaik(idadmin, tokenAuth)

        halBaikBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        halBaikBinding.fabAddGoodThings.setOnClickListener {
            addHalBaikBinding = BottomSheetHalBaikBinding.inflate(layoutInflater)

            val dialog = BottomSheetDialog(this@HalBaikActivity)
            val btnSave = addHalBaikBinding.btnSaveHalBaik

            btnSave.setOnClickListener {
                halBaikBinding.loadingAnim.visibility = View.VISIBLE
                val halBaik = addHalBaikBinding.inputHalBaik.text.toString()
                val service = RetrofitClient().apiRequest().create(DataService::class.java)
                service.insertHalBaik(idadmin, halBaik, tokenAuth).enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                getHalBaik(idadmin, tokenAuth)
                                Toasty.success(this@HalBaikActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                            }
                        } else {
                            ErrorHandler().responseHandler(this@HalBaikActivity, response.message())
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        halBaikBinding.loadingAnim.visibility = View.GONE
                        ErrorHandler().responseHandler(this@HalBaikActivity, t.message.toString())
                    }
                })
                dialog.dismiss()
            }
            dialog.setCancelable(true)
            dialog.setContentView(addHalBaikBinding.root)
            dialog.show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@HalBaikActivity, MainActivity::class.java))
        finish()
    }

    private fun getHalBaik(idadmin: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getHalBaik(idadmin, tokenAuth).enqueue(object : Callback<HalBaikResponse> {
            override fun onResponse(call: Call<HalBaikResponse>, response: Response<HalBaikResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        halBaikBinding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        listHalBaik = listData
                        halBaikAdapter.setHalBaikItem(listHalBaik)
                        halBaikAdapter.notifyDataSetChanged()

                        with(halBaikBinding.rvHalBaik) {
                            layoutManager = LinearLayoutManager(this@HalBaikActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = halBaikAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        halBaikBinding.empty.visibility = View.VISIBLE
                        halBaikBinding.loadingAnim.visibility = View.GONE
                        listHalBaik.clear()
                        halBaikAdapter.setHalBaikItem(listHalBaik)
                        halBaikAdapter.notifyDataSetChanged()
                    }
                } else {
                    ErrorHandler().responseHandler(this@HalBaikActivity, response.message())
                }
            }

            override fun onFailure(call: Call<HalBaikResponse>, t: Throwable) {
                halBaikBinding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(this@HalBaikActivity, t.message.toString())
            }
        })
    }

}