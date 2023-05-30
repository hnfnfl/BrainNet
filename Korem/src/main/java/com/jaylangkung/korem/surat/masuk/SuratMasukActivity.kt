package com.jaylangkung.korem.surat.masuk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.SuratMasukResponse
import com.jaylangkung.korem.databinding.ActivitySuratMasukBinding
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.retrofit.SuratService
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuratMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuratMasukBinding
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
                startActivity(Intent(this@SuratMasukActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSuratMasuk("", "", "", tokenAuth)
        binding.apply {

        }
    }

    private fun getSuratMasuk(sumber: String, bentuk: String, disposisi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.getSuratMasuk(sumber, bentuk, disposisi, tokenAuth).enqueue(object : Callback<SuratMasukResponse> {
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
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        adapter.setItem(emptyList())
                        adapter.notifyItemRangeChanged(0, 0)
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