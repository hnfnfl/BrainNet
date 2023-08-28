package com.jaylangkung.eoffice_korem.surat.masuk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.MainActivity
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.SuratMasukResponse
import com.jaylangkung.eoffice_korem.databinding.ActivitySuratMasukBinding
import com.jaylangkung.eoffice_korem.databinding.BottomSheetFilterSuratMasukBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuratMasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuratMasukBinding
    private lateinit var bottomSheetFilterSuratMasukBinding: BottomSheetFilterSuratMasukBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var adapter: SuratMasukAdapter

    private var nomerAgenda = ""
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

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val jabatan = myPreferences.getValue(Constants.USER_JABATAN).toString()
        nomerAgenda = intent.getStringExtra("nomerAgenda").toString()
        var disposisi: String
        var sumber = ""

        binding.apply {
            empty.visibility = View.GONE
            btnBack.setOnClickListener {
                empty.visibility = View.GONE
                llMiliterNonmiliter.visibility = View.VISIBLE
                rvSuratMasukList.visibility = View.INVISIBLE
                btnBack.setOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

            disposisi = if (jabatan.contains("Danrem")) {
                "danrem"
            } else if (jabatan.contains("Kasrem")) {
                "kasrem"
            } else {
                ""
            }

            if (nomerAgenda != "") {
                binding.llMiliterNonmiliter.visibility = View.GONE
                getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
            }

            btnMiliter.setOnClickListener {
                sumber = "militer"
                getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
                llMiliterNonmiliter.visibility = View.GONE
            }

            btnNonmiliter.setOnClickListener {
                sumber = "non_militer"
                getSuratMasuk(sumber, "", "", disposisi, tokenAuth)
                llMiliterNonmiliter.visibility = View.GONE
            }

            fabFilter.setOnClickListener {
                bottomSheetFilterSuratMasukBinding = BottomSheetFilterSuratMasukBinding.inflate(layoutInflater)
                val dialog = BottomSheetDialog(this@SuratMasukActivity).apply {
                    setCancelable(true)
                    setContentView(bottomSheetFilterSuratMasukBinding.root)
                }

                bottomSheetFilterSuratMasukBinding.apply {
                    val listBentukSurat = ArrayList<String>()
                    val listSumberSuratNext = ArrayList<String>()

                    var bentuk = ""
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
                        llMiliterNonmiliter.visibility = View.GONE
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

                        // if nomerAgenda is not empty, then go to position of recyclerview item
                        if (nomerAgenda != "") {
                            for (i in listData.indices) {
                                if (listData[i].nomerAgenda == nomerAgenda) {
                                    binding.rvSuratMasukList.smoothScrollToPosition(i)
                                    break
                                }
                            }
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