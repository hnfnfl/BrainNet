package com.jaylangkung.korem.survey

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.korem.MainActivity
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.DefaultResponse
import com.jaylangkung.korem.dataClass.SurveyData
import com.jaylangkung.korem.dataClass.SurveyResponse
import com.jaylangkung.korem.databinding.ActivitySurveyBinding
import com.jaylangkung.korem.retrofit.DataService
import com.jaylangkung.korem.retrofit.RetrofitClient
import com.jaylangkung.korem.utils.Constants
import com.jaylangkung.korem.utils.ErrorHandler
import com.jaylangkung.korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var surveyAdapter: SurveyPertanyaanAdapter
    private var pertanyaanList: ArrayList<SurveyData> = arrayListOf()

    companion object {
        var jawabanList = mutableMapOf<Int, String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@SurveyActivity)
        surveyAdapter = SurveyPertanyaanAdapter()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                jawabanList.clear()
                startActivity(Intent(this@SurveyActivity, MainActivity::class.java))
                finish()
            }
        })

        val iduser = myPreferences.getValue(Constants.USER_IDAKTIVASI).toString()
        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        getSurvey(iduser, tokenAuth)
        binding.apply {
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            btnKirimSurvei.setOnClickListener {
                if (jawabanList.size == pertanyaanList.size) {
                    var dataJawaban = ""
                    for (data in jawabanList) {
                        dataJawaban += "|${data.key},${data.value}"
                    }
                    insertSurvey(iduser, dataJawaban, tokenAuth)
                } else {
                    Toasty.warning(this@SurveyActivity, "mohon lengkapi survei", Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getSurvey(iduser_aktivasi: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getSurvey(iduser_aktivasi, tokenAuth).enqueue(object : Callback<SurveyResponse> {
            override fun onResponse(call: Call<SurveyResponse>, response: Response<SurveyResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        val listData = response.body()!!.data
                        pertanyaanList = listData
                        surveyAdapter.setItem(pertanyaanList)
                        surveyAdapter.notifyItemRangeChanged(0, pertanyaanList.size)

                        with(binding.rvUserList) {
                            layoutManager = LinearLayoutManager(this@SurveyActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = surveyAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        binding.btnKirimSurvei.visibility = View.GONE
                        Toasty.info(this@SurveyActivity, "Anda sudah mengisi semua survey", Toasty.LENGTH_LONG).show()
                        pertanyaanList.clear()
                        surveyAdapter.setItem(pertanyaanList)
                        surveyAdapter.notifyItemRangeChanged(0, pertanyaanList.size)
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@SurveyActivity,
                        "getSurvey | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<SurveyResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@SurveyActivity,
                    "getSurvey | onFailure", t.message.toString()
                )
            }
        })
    }

    private fun insertSurvey(iduser_aktivasi: String, jawaban: String, tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.insertSurvey(iduser_aktivasi, jawaban, tokenAuth).enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        Toasty.success(this@SurveyActivity, response.body()!!.message, Toasty.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@SurveyActivity,
                        "insertSurvey | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@SurveyActivity,
                    "insertSurvey | onFailure", t.message.toString()
                )
            }
        })
    }
}