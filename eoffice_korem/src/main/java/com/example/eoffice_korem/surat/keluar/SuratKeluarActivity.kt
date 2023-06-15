package com.example.eoffice_korem.surat.keluar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eoffice_korem.MainActivity2
import com.example.eoffice_korem.R
import com.example.eoffice_korem.dataClass.SuratKeluarResponse
import com.example.eoffice_korem.databinding.ActivitySuratKeluarBinding
import com.example.eoffice_korem.databinding.BottomSheetFilterSuratMasukBinding
import com.example.eoffice_korem.retrofit.RetrofitClient
import com.example.eoffice_korem.retrofit.SuratService
import com.example.eoffice_korem.utils.Constants
import com.example.eoffice_korem.utils.ErrorHandler
import com.example.eoffice_korem.utils.MySharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuratKeluarActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuratKeluarBinding
    private lateinit var bottomSheetFilterSuratMasukBinding: BottomSheetFilterSuratMasukBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: SuratKeluarAdapter

    private var bentuk: String = ""
    private var disposisi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratKeluarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SuratKeluarActivity)
        adapter = SuratKeluarAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(
                    Intent(
                        this@SuratKeluarActivity,
                        com.example.eoffice_korem.MainActivity2::class.java
                    )
                )
                finish()
            }
        })

        val tokenAuth =
            getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSuratKeluar("", "", tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            fabFilter.setOnClickListener {
                bottomSheetFilterSuratMasukBinding = BottomSheetFilterSuratMasukBinding.inflate(layoutInflater)
                val dialog = BottomSheetDialog(this@SuratKeluarActivity).apply {
                    setCancelable(true)
                    setContentView(bottomSheetFilterSuratMasukBinding.root)
                }

                bottomSheetFilterSuratMasukBinding.apply {
                    sumberSurat1Spinner.visibility = View.GONE
                    sumberSurat2Spinner.visibility = View.GONE

                    val listBentukSurat = ArrayList<String>()
                    val listDisposisSurat = ArrayList<String>()

                    listBentukSurat.addAll(
                        listOf(
                            "All",
                            "Biasa",
                            "Brafak",
                            "Bratel",
                            "DILMIL",
                            "Direktif",
                            "Hibah",
                            "KEP/SKEP",
                            "NHV",
                            "Nota Dinas",
                            "SPRIN",
                            "ST",
                            "STR",
                            "Surat Cuti",
                            "Surat Edaran",
                            "Telegram",
                            "Undangan"
                        )
                    )
                    bentukSpinner.item = listBentukSurat as ArrayList<*>?
                    listDisposisSurat.add("All")
                    for (i in 0 until MainActivity2.listUserSurat.size) {
                        val rawName = MainActivity2.listUserSurat[i].nama.substringAfter("(").substringBefore(")")
                        val name = rawName.substringBefore("korem 083/bdj").trim()
                        listDisposisSurat.add(name)
                    }
                    disposisiSpinner.item = listDisposisSurat as ArrayList<*>?

                    bentukSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            bentuk = if (listBentukSurat[p2] == "All" || listBentukSurat[p2] == "") {
                                ""
                            } else {
                                listBentukSurat[p2]
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }

                    disposisiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            disposisi = if (listDisposisSurat[p2] == "All" || listDisposisSurat[p2] == "") {
                                ""
                            } else {
                                listDisposisSurat[p2]
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }

                    btnApplyFilter.setOnClickListener {
                        getSuratKeluar(bentuk, disposisi, tokenAuth)
                        dialog.dismiss()
                    }
                }

                dialog.show()
            }

            fabAddSuratKeluar.setOnClickListener {
                startActivity(
                    Intent(
                        this@SuratKeluarActivity,
                        TambahSuratKeluarActivity::class.java
                    )
                )
                finish()
            }
        }


    }

    private fun getSuratKeluar(bentuk: String, disposisi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.getSuratKeluar(bentuk, disposisi, tokenAuth)
            .enqueue(object : Callback<SuratKeluarResponse> {
                override fun onResponse(
                    call: Call<SuratKeluarResponse>,
                    response: Response<SuratKeluarResponse>
                ) {
                    if (response.isSuccessful) {
                        val listData = response.body()!!.data
                        if (response.body()!!.status == "success") {
                            binding.loadingAnim.visibility = View.GONE
                            adapter.setItem(listData)
                            adapter.notifyItemRangeChanged(0, listData.size)

                            with(binding.rvSuratKeluarList) {
                                layoutManager = LinearLayoutManager(this@SuratKeluarActivity)
                                itemAnimator = DefaultItemAnimator()
                                setHasFixedSize(true)
                                adapter = this@SuratKeluarActivity.adapter
                            }
                        } else if (response.body()!!.status == "empty") {
                            binding.apply {
                                empty.visibility = View.VISIBLE
                                loadingAnim.visibility = View.GONE
                            }
                            adapter.clearItem()
                        }
                    } else {
                        binding.loadingAnim.visibility = View.GONE
                        ErrorHandler().responseHandler(
                            this@SuratKeluarActivity,
                            "getSuratKeluar | onResponse", response.message()
                        )
                    }
                }

                override fun onFailure(call: Call<SuratKeluarResponse>, t: Throwable) {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@SuratKeluarActivity,
                        "getSuratKeluar | onFailure", t.message.toString()
                    )
                }
            })
    }
}