package com.example.eoffice_korem.surat.masuk

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
import com.example.eoffice_korem.dataClass.SuratMasukResponse
import com.example.eoffice_korem.databinding.ActivitySuratMasukBinding
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

class SuratMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuratMasukBinding
    private lateinit var bottomSheetFilterSuratMasukBinding: BottomSheetFilterSuratMasukBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: SuratMasukAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratMasukBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SuratMasukActivity)
        adapter = SuratMasukAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SuratMasukActivity, MainActivity2::class.java))
                finish()
            }
        })

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        var disposisi = ""
        var sumber = ""

        binding.apply {
            empty.visibility = View.GONE
            btnBack.setOnClickListener {
                llFilter.visibility = View.VISIBLE
                llDanremKasrem.visibility = View.GONE
                llMiliterNonmiliter.visibility = View.VISIBLE
                rvSuratMasukList.visibility = View.INVISIBLE
                btnBack.setOnClickListener {
                    llMiliterNonmiliter.visibility = View.GONE
                    llDanremKasrem.visibility = View.VISIBLE
                    if (llDanremKasrem.visibility == View.VISIBLE) {
                        btnBack.setOnClickListener {
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }

            btnDanrem.setOnClickListener {
                llMiliterNonmiliter.visibility = View.VISIBLE
                llDanremKasrem.visibility = View.GONE
                disposisi = "danrem"
                btnMiliter.setOnClickListener {
                    llFilter.visibility = View.GONE
                    sumber = "militer"
                    getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
                }

                btnNonmiliter.setOnClickListener {
                    llFilter.visibility = View.GONE
                    sumber = "non_militer"
                    getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
                }
            }

            btnKasrem.setOnClickListener {
                llMiliterNonmiliter.visibility = View.VISIBLE
                llDanremKasrem.visibility = View.GONE
                disposisi = "kasrem"
                btnMiliter.setOnClickListener {
                    llFilter.visibility = View.GONE
                    sumber = "militer"
                    getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
                }

                btnNonmiliter.setOnClickListener {
                    llFilter.visibility = View.GONE
                    sumber = "non_militer"
                    getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
                }
            }

            fabFilter.setOnClickListener {
                bottomSheetFilterSuratMasukBinding = BottomSheetFilterSuratMasukBinding.inflate(layoutInflater)
                val dialog = BottomSheetDialog(this@SuratMasukActivity).apply {
                    setCancelable(true)
                    setContentView(bottomSheetFilterSuratMasukBinding.root)
                }

                bottomSheetFilterSuratMasukBinding.apply {
                    val listBentukSurat = ArrayList<String>()
//                    val listDisposisSurat = ArrayList<String>()
                    val listSumberSurat = ArrayList<String>()
                    val listSumberSuratNext = ArrayList<String>()

                    var bentuk = ""
//                    var disposisi = ""
//                    var sumber = ""
                    var sumberNext = ""

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
//                    listDisposisSurat.add("All")
//                    for (i in 0 until listUserSurat.size) {
//                        val rawName = listUserSurat[i].nama.substringAfter("(").substringBefore(")")
//                        val name = rawName.replace("Korem 083/Bdj".toRegex(), "")
//                        listDisposisSurat.add(name)
//                    }
//                    disposisiSpinner.item = listDisposisSurat as ArrayList<*>?

//                    listSumberSurat.addAll(
//                        listOf("All", "Militer", "Non Militer")
//                    )
//                    sumberSurat1Spinner.item = listSumberSurat as ArrayList<*>?

                    listSumberSuratNext.addAll(
                        listOf("All", "Komando Atas", "Satuan Samping", "Jajaran Korem")
                    )
                    sumberSurat2Spinner.item = listSumberSuratNext as ArrayList<*>?

                    bentukSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            bentuk = if (listBentukSurat[p2] == "All" || listBentukSurat[p2] == "") {
                                ""
                            } else {
                                listBentukSurat[p2]
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            bentuk = ""
                        }
                    }

//                    disposisiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                            disposisi = if (listDisposisSurat[p2] == "All" || listDisposisSurat[p2] == "") {
//                                ""
//                            } else {
//                                listDisposisSurat[p2]
//                            }
//                        }
//
//                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            disposisi = ""
//                        }
//                    }

//                    sumberSurat1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                            sumber = if (listSumberSurat[p2] == "All" || listSumberSurat[p2] == "") {
//                                ""
//                            } else {
//                                listSumberSurat[p2]
//                            }
//                        }
//
//                        override fun onNothingSelected(p0: AdapterView<*>?) {
//                            sumber = ""
//                        }
//                    }

                    sumberSurat2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            sumberNext = if (listSumberSuratNext[p2] == "All" || listSumberSuratNext[p2] == "") {
                                ""
                            } else {
                                listSumberSuratNext[p2]
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                            sumberNext = ""
                        }
                    }

                    btnApplyFilter.setOnClickListener {
                        llFilter.visibility = View.GONE
                        getSuratMasuk(sumber, sumberNext, bentuk, disposisi, tokenAuth)
                        dialog.dismiss()
                    }
                }

                dialog.show()
            }

            fabAddSuratMasuk.setOnClickListener {
                startActivity(Intent(this@SuratMasukActivity, TambahSuratMasukActivity::class.java))
                finish()
            }
        }
    }


    private fun getSuratMasuk(sumber: String, sumberNext: String, bentuk: String, disposisi: String, tokenAuth: String) {
        binding.loadingAnim.visibility = View.VISIBLE
        binding.rvSuratMasukList.visibility = View.VISIBLE
        val service = RetrofitClient().apiRequest().create(SuratService::class.java)
        service.getSuratMasuk(sumber, sumberNext, bentuk, disposisi, tokenAuth).enqueue(object : Callback<SuratMasukResponse> {
            override fun onResponse(call: Call<SuratMasukResponse>, response: Response<SuratMasukResponse>) {
                if (response.isSuccessful) {
                    val listData = response.body()!!.data
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        binding.empty.visibility = View.GONE
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
                        adapter.clearItem()
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@SuratMasukActivity, "getSuratMasuk | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<SuratMasukResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@SuratMasukActivity, "getSuratMasuk | onFailure", t.message.toString()
                )
            }
        })
    }
}