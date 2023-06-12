package com.example.eoffice_korem.surat.masuk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eoffice_korem.R
import com.example.eoffice_korem.dataClass.SuratMasukResponse
import com.example.eoffice_korem.databinding.ActivitySuratMasukBinding
import com.example.eoffice_korem.databinding.BottomSheetFilterSuratMasukBinding
import com.example.eoffice_korem.retrofit.RetrofitClient
import com.example.eoffice_korem.retrofit.SuratService
import com.example.eoffice_korem.utils.Constants
import com.example.eoffice_korem.utils.ErrorHandler
import com.example.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuratMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuratMasukBinding
    private lateinit var bottomSheetFilterSuratMasukBinding: BottomSheetFilterSuratMasukBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: SuratMasukAdapter

    private var sumber: String = ""
    private var bentuk: String = ""
    private var disposisi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SuratMasukActivity)
        adapter = SuratMasukAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SuratMasukActivity, com.example.eoffice_korem.MainActivity2::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSuratMasuk("", "", "", "", tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            fabFilter.setOnClickListener {
                Toasty.info(this@SuratMasukActivity, "Fitur ini akan segera hadir", Toasty.LENGTH_SHORT).show()
//                bottomSheetFilterSuratMasukBinding = BottomSheetFilterSuratMasukBinding.inflate(layoutInflater)
//                val dialog = BottomSheetDialog(this@SuratMasukActivity).apply {
//                    setCancelable(true)
//                    setContentView(bottomSheetFilterSuratMasukBinding.root)
//                }
//
//                dialog.show()
            }

            fabAddSuratMasuk.setOnClickListener {
                startActivity(Intent(this@SuratMasukActivity, TambahSuratMasukActivity::class.java))
                finish()
            }
        }
    }


    private fun getSuratMasuk(sumber: String, sumberNext: String, bentuk: String, disposisi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.getSuratMasuk(sumber, sumberNext, bentuk, disposisi, tokenAuth).enqueue(object : Callback<SuratMasukResponse> {
            override fun onResponse(call: Call<SuratMasukResponse>, response: Response<SuratMasukResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        adapter.setItem(listData)
                        adapter.notifyItemRangeChanged(0, listData.size)

                        with(binding.rvSuratMasukList) {
                            layoutManager = LinearLayoutManager(this@SuratMasukActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = this@SuratMasukActivity.adapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        binding.apply {
                            empty.visibility = View.VISIBLE
                            loadingAnim.visibility = View.GONE
                        }
                        adapter.apply {
                            setItem(emptyList())
                            notifyItemRangeChanged(0, 0)
                        }
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@SuratMasukActivity,
                        "getSuratMasuk | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<SuratMasukResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@SuratMasukActivity,
                    "getSuratMasuk | onFailure", t.message.toString()
                )
            }
        })
    }
}