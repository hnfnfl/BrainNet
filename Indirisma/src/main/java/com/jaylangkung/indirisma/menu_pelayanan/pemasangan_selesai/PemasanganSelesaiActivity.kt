package com.jaylangkung.indirisma.menu_pelayanan.pemasangan_selesai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityPemasanganSelesaiBinding
import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.DataSpinnerEntity
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.BelumTerpasangResponse
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PemasanganSelesaiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPemasanganSelesaiBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var pemasanganSelesaiAdapter: PemasanganSelesaiAdapter
    private var listPemasangan: ArrayList<DataSpinnerEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemasanganSelesaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@PemasanganSelesaiActivity)
        pemasanganSelesaiAdapter = PemasanganSelesaiAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        getBelumTerpasang(tokenAuth)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@PemasanganSelesaiActivity, MainActivity::class.java))
        finish()
    }

    private fun getBelumTerpasang(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getBelumTerpasang(tokenAuth, "true").enqueue(object : Callback<BelumTerpasangResponse> {
            override fun onResponse(call: Call<BelumTerpasangResponse>, response: Response<BelumTerpasangResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        binding.empty.visibility = View.GONE
                        val listData = response.body()!!.data
                        listPemasangan = listData
                        pemasanganSelesaiAdapter.setItem(listPemasangan)
                        pemasanganSelesaiAdapter.notifyItemRangeChanged(0, listPemasangan.size)

                        with(binding.rvPemasanganSelesai) {
                            layoutManager = LinearLayoutManager(this@PemasanganSelesaiActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = pemasanganSelesaiAdapter
                        }
                        pemasanganSelesaiAdapter.setOnItemClickCallback(object : PemasanganSelesaiAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ArrayList<DataSpinnerEntity>, position: Int) {
                                val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
                                val idpelanggan = listPemasangan[position].idpelanggan

                                val mDialog = MaterialDialog.Builder(this@PemasanganSelesaiActivity as Activity)
                                    .setTitle("Selesaikan Pemasangan")
                                    .setMessage("Apakah Anda yakin ingin menyelesaikan pemasangan ini?")
                                    .setCancelable(true)
                                    .setPositiveButton(getString(R.string.yes), R.drawable.ic_checked)
                                    { dialogInterface, _ ->
                                        service.insertBayarTeknisi(idpelanggan, idadmin, tokenAuth, "true")
                                            .enqueue(object : Callback<DefaultResponse> {
                                                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                                                    if (response.isSuccessful) {
                                                        if (response.body()!!.status == "success") {
                                                            Toasty.success(
                                                                this@PemasanganSelesaiActivity,
                                                                "Pemasangan berhasil diselesaikan",
                                                                Toasty.LENGTH_LONG
                                                            ).show()
                                                            listPemasangan.removeAt(position)
                                                            if (listPemasangan.isEmpty()) {
                                                                binding.empty.visibility = View.VISIBLE
                                                                binding.loadingAnim.visibility = View.GONE
                                                                listPemasangan.clear()
                                                            }
                                                            pemasanganSelesaiAdapter.setItem(listPemasangan)
                                                            pemasanganSelesaiAdapter.notifyItemRangeChanged(0, listPemasangan.size)
                                                        }
                                                    } else {
                                                        binding.loadingAnim.visibility = View.GONE
                                                        ErrorHandler().responseHandler(
                                                            this@PemasanganSelesaiActivity,
                                                            "insertBayarTeknisi | onResponse", response.message()
                                                        )
                                                    }
                                                }

                                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                                    binding.loadingAnim.visibility = View.GONE
                                                    ErrorHandler().responseHandler(
                                                        this@PemasanganSelesaiActivity,
                                                        "insertBayarTeknisi | onResponse", t.message.toString()
                                                    )
                                                }
                                            })
                                        dialogInterface.dismiss()
                                    }
                                    .setNegativeButton(getString(R.string.no), R.drawable.ic_close)
                                    { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .build()
                                // Show Dialog
                                mDialog.show()
                            }

                        })
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        listPemasangan.clear()
                        pemasanganSelesaiAdapter.setItem(listPemasangan)
                        pemasanganSelesaiAdapter.notifyItemRangeChanged(0, listPemasangan.size)
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@PemasanganSelesaiActivity,
                        "getBelumTerpasang | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<BelumTerpasangResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@PemasanganSelesaiActivity,
                    "getBelumTerpasang | onFailure", t.message.toString()
                )
            }

        })
    }
}