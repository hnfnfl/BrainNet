package com.example.eoffice_korem.surat.keluar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.eoffice_korem.MainActivity2.Companion.listUserSurat
import com.example.eoffice_korem.R
import com.example.eoffice_korem.dataClass.DefaultResponse
import com.example.eoffice_korem.dataClass.SuratKeluarData
import com.example.eoffice_korem.databinding.BottomSheetDisposisiSuratBinding
import com.example.eoffice_korem.databinding.BottomSheetGambarSuratBinding
import com.example.eoffice_korem.databinding.BottomSheetRiwayatDisposisiBinding
import com.example.eoffice_korem.databinding.ItemSuratKeluarBinding
import com.example.eoffice_korem.retrofit.RetrofitClient
import com.example.eoffice_korem.retrofit.SuratService
import com.example.eoffice_korem.utils.Constants
import com.example.eoffice_korem.utils.ErrorHandler
import com.example.eoffice_korem.utils.MySharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuratKeluarAdapter : RecyclerView.Adapter<SuratKeluarAdapter.ItemHolder>() {

    private var list = ArrayList<SuratKeluarData>()
    fun setItem(item: List<SuratKeluarData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItem() {
        this.list.clear()
        notifyDataSetChanged()
    }

    class ItemHolder(private val binding: ItemSuratKeluarBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var bottomSheetRiwayatDisposisiBinding: BottomSheetRiwayatDisposisiBinding
        private lateinit var bottomSheetDisposisiSuratBinding: BottomSheetDisposisiSuratBinding
        private lateinit var bottomSheetGambarSuratBinding: BottomSheetGambarSuratBinding
        private lateinit var myPreferences: MySharedPreferences

        private var idPenerima: String = ""

        fun bind(item: SuratKeluarData) {
            myPreferences = MySharedPreferences(itemView.context)
            val tokenAuth = itemView.context.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
            val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()

            binding.apply {
                tvSkNomorAgenda.text = item.nomer_agenda
                tvSkPenerima.text = itemView.context.getString(R.string.sm_penerima, item.penerima)
                tvSkPerihal.text = itemView.context.getString(R.string.sk_perihal, item.perihal)
                tvSkDibuat.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.tanggal_surat)
                tvSkStatus.text = itemView.context.getString(R.string.cuti_status_view, item.status_surat_keluar)

                btnSkTeruskan.setOnClickListener {
                    bottomSheetDisposisiSuratBinding = BottomSheetDisposisiSuratBinding.inflate(LayoutInflater.from(itemView.context))
                    val dialog = BottomSheetDialog(itemView.context).apply {
                        setCancelable(true)
                        setContentView(bottomSheetDisposisiSuratBinding.root)
                    }

                    bottomSheetDisposisiSuratBinding.apply {
                        val listUser = java.util.ArrayList<String>()
                        for (i in 0 until listUserSurat.size) {
                            listUser.add(listUserSurat[i].nama)
                        }
                        penerimaDisposisiSpinner.item = listUser as List<Any>?
                        penerimaDisposisiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                idPenerima = listUserSurat[p2].idsurat_user_aktivasi
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {}
                        }

                        btnSendDisposisi.setOnClickListener {
                            btnSendDisposisi.startAnimation()
                            if (idPenerima != "") {
                                val catatan = inputDisposisiCatatan.text.toString()
                                insertSuratDisposisi(iduser, item.idsurat_keluar, catatan, idPenerima, tokenAuth)
                                idPenerima = ""
                                dialog.dismiss()
                            } else {
                                btnSendDisposisi.endAnimation()
                                Toasty.warning(itemView.context, "Penerima Terusan Tidak Boleh Kosong", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    dialog.show()
                }

                if (item.status_surat_keluar == "draft") {
                    btnSkAjukan.visibility = View.VISIBLE
                    btnSkAjukan.setOnClickListener {
                        editSuratKeluar(item.idsurat_keluar, tokenAuth)
                    }
                } else {
                    btnSkAjukan.visibility = View.GONE
                }

                btnSkImg.setOnClickListener {
                    item.img?.let {
                        if (it.isNotEmpty()) {
                            bottomSheetGambarSuratBinding = BottomSheetGambarSuratBinding.inflate(LayoutInflater.from(itemView.context))
                            val dialog = BottomSheetDialog(itemView.context).apply {
                                setCancelable(true)
                                setContentView(bottomSheetGambarSuratBinding.root)
                            }

                            bottomSheetGambarSuratBinding.apply {
                                val imgList = ArrayList<SlideModel>()
                                for (img in item.img) {
                                    imgList.add(SlideModel(img.img))
                                }
                                imgsliderGiat.setImageList(imgList)
                            }

                            dialog.show()
                        } else {
                            Toasty.info(itemView.context, "Tidak ada gambar", Toast.LENGTH_SHORT, true).show()
                        }
                    }
                }

                if (item.riwayat.toInt() != 0) {
                    btnSkRiwayat.visibility = View.VISIBLE
                    btnSkRiwayat.setOnClickListener {
                        bottomSheetRiwayatDisposisiBinding = BottomSheetRiwayatDisposisiBinding.inflate(LayoutInflater.from(itemView.context))
                        val dialog = BottomSheetDialog(itemView.context).apply {
                            setCancelable(true)
                            setContentView(bottomSheetRiwayatDisposisiBinding.root)
                            behavior.maxHeight = 1500
                        }

                        bottomSheetRiwayatDisposisiBinding.apply {
                            for (data in item.riwayat_disposisi) {
                                val llChild = LinearLayout(itemView.context).apply {
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

                                val separator = View(itemView.context).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, 4
                                    ).apply { setMargins(0, 16, 0, 0) }
                                    setBackgroundColor(Color.parseColor("#c0c0c0"))
                                }

                                val createTextView = { text: String, size: Float ->
                                    TextView(itemView.context).apply {
                                        this.text = text
                                        setTextColor(itemView.context.getColor(R.color.black))
                                        textSize = size
                                        layoutParams = tvParams
                                    }
                                }

                                llChild.addView(createTextView("${data.nomer_agenda} (${data.aksi})", 16.0F))
                                llChild.addView(createTextView(data.tanggal_disposisi, 16.0F))
                                llChild.addView(createTextView(itemView.context.getString(R.string.sm_pengirim, data.pengirim), 16.0F))
                                llChild.addView(createTextView(itemView.context.getString(R.string.sm_penerima, data.penerima), 16.0F))
                                llChild.addView(separator)

                                linearlayout.addView(llChild)
                            }
                        }
                        dialog.show()
                    }
                } else {
                    btnSkRiwayat.visibility = View.GONE
                }
            }
        }

        private fun editSuratKeluar(idsurat: String, tokenAuth: String) {
            val service = RetrofitClient().apiRequest().create(SuratService::class.java)
            service.editSuratKeluar(idsurat, "", "", "", "", "pengajuan", tokenAuth)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                Toasty.success(itemView.context, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                itemView.context.startActivity(Intent(itemView.context, SuratKeluarActivity::class.java))
                                (itemView.context as Activity).finish()
                            }
                        } else {
                            ErrorHandler().responseHandler(
                                itemView.context,
                                "insertSuratDisposisi | onResponse", response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        ErrorHandler().responseHandler(
                            itemView.context,
                            "insertSuratDisposisi | onFailure", t.message.toString()
                        )
                    }
                })
        }

        private fun insertSuratDisposisi(iduser: String, idsurat: String, catatan: String, penerima: String, tokenAuth: String) {
            val service = RetrofitClient().apiRequest().create(SuratService::class.java)
            service.insertSuratDisposisi(iduser, idsurat, "surat_keluar", "terusan", catatan, penerima, tokenAuth)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                Toasty.success(itemView.context, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                itemView.context.startActivity(Intent(itemView.context, SuratKeluarActivity::class.java))
                                (itemView.context as Activity).finish()
                            }
                        } else {
                            ErrorHandler().responseHandler(
                                itemView.context,
                                "insertSuratDisposisi | onResponse", response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        ErrorHandler().responseHandler(
                            itemView.context,
                            "insertSuratDisposisi | onFailure", t.message.toString()
                        )
                    }
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemSuratKeluarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}