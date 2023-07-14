package com.jaylangkung.eoffice_korem.surat

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.SuratRiwayatDisposisi
import com.jaylangkung.eoffice_korem.databinding.BottomSheetDisposisiBalasanBinding
import com.jaylangkung.eoffice_korem.databinding.BottomSheetRiwayatDisposisiBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun showDisposisiRiwayat(ctx: Context, data: ArrayList<SuratRiwayatDisposisi>) {
    val binding: BottomSheetRiwayatDisposisiBinding = BottomSheetRiwayatDisposisiBinding.inflate(LayoutInflater.from(ctx))
    val dialog = BottomSheetDialog(ctx).apply {
        setCancelable(true)
        setContentView(binding.root)
        behavior.maxHeight = 1500
    }

    binding.apply {
        for (d in data) {
            val llChild = LinearLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
                orientation = LinearLayout.VERTICAL
            }

            val tvParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(10, 6, 0, 0)
            }

            val separator = View(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 4
                ).apply { setMargins(0, 16, 0, 0) }
                setBackgroundColor(Color.parseColor("#c0c0c0"))
            }

            val createTextView = { text: String, size: Float ->
                TextView(ctx).apply {
                    this.text = text
                    setTextColor(ctx.getColor(R.color.black))
                    textSize = size
                    layoutParams = tvParams
                }
            }

            llChild.addView(createTextView("${d.nomer_agenda} (${d.aksi})", 16.0F))
            llChild.addView(createTextView(d.tanggal_disposisi, 16.0F))
            llChild.addView(createTextView(ctx.getString(R.string.dispo_pengirim, d.pengirim), 16.0F))
            llChild.addView(createTextView(ctx.getString(R.string.dispo_penerima, d.penerima), 16.0F))
            if (d.catatan.isNotEmpty()) {
                llChild.addView(createTextView(ctx.getString(R.string.dispo_catatan, d.catatan), 16.0F))
            }
            if (d.catatan_tambahan.isNotEmpty()) {
                llChild.addView(createTextView(ctx.getString(R.string.dispo_catatan_tambahan, d.catatan_tambahan), 16.0F))
            }
            llChild.addView(createTextView(ctx.getString(R.string.dispo_balasan, d.balasan), 16.0F))
            if (d.balasan.isNullOrBlank()) {
                // tambahkan tombol balas
                val btnBalas = Button(ctx).apply {
                    text = ctx.getString(R.string.dispo_balas)
                    backgroundTintList = AppCompatResources.getColorStateList(ctx, R.color.primaryColor)
                    setTextColor(ctx.getColor(R.color.white))
                    background = AppCompatResources.getDrawable(ctx, R.drawable.rounded_corner)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 12, 0, 0)
                    }
                    setOnClickListener {
                        dialog.dismiss()
                        showDisposisiBalas(ctx, d.nomer_agenda)
                    }
                }
                llChild.addView(btnBalas)
            }
            llChild.addView(separator)

            linearlayout.addView(llChild)
        }
    }

    dialog.show()
}

fun showDisposisiBalas(ctx: Context, nomerAgenda: String) {
    val myPreferences = MySharedPreferences(ctx)
    val binding: BottomSheetDisposisiBalasanBinding = BottomSheetDisposisiBalasanBinding.inflate(LayoutInflater.from(ctx))
    val dialog = BottomSheetDialog(ctx).apply {
        setCancelable(true)
        setContentView(binding.root)
    }

    binding.apply {
        btnSendBalasan.setOnClickListener {
            val tokenAuth = ctx.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
            val balasan = inputDisposisiBalasan.text.toString()
            val service = RetrofitClient().apiRequest().create(SuratService::class.java)
            service.editSuratDisposisi(nomerAgenda, balasan, tokenAuth).enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                Toasty.success(ctx, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                        } else {
                            ErrorHandler().responseHandler(
                                ctx, "insertSuratDisposisi | onResponse", response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        ErrorHandler().responseHandler(
                            ctx, "insertSuratDisposisi | onFailure", t.message.toString()
                        )
                    }
                })
        }
    }

    dialog.show()
}