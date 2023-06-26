package com.jaylangkung.eoffice_korem.surat.masuk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.eoffice_korem.MainActivity2.Companion.listUserSurat
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.SuratMasukData
import com.jaylangkung.eoffice_korem.databinding.BottomSheetDisposisiSuratBinding
import com.jaylangkung.eoffice_korem.databinding.BottomSheetGambarSuratBinding
import com.jaylangkung.eoffice_korem.databinding.BottomSheetRiwayatDisposisiBinding
import com.jaylangkung.eoffice_korem.databinding.ItemSuratMasukBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SuratMasukAdapter : RecyclerView.Adapter<SuratMasukAdapter.ItemHolder>() {

    private var list = ArrayList<SuratMasukData>()
    fun setItem(item: List<SuratMasukData>?) {
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

    class ItemHolder(private val binding: ItemSuratMasukBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var bottomSheetRiwayatDisposisiBinding: BottomSheetRiwayatDisposisiBinding
        private lateinit var bottomSheetDisposisiSuratBinding: BottomSheetDisposisiSuratBinding
        private lateinit var bottomSheetGambarSuratBinding: BottomSheetGambarSuratBinding
        private lateinit var myPreferences: MySharedPreferences

        private var idPenerima: String = ""
        private var catatanTambahan: String = ""

        fun bind(item: SuratMasukData) {
            myPreferences = MySharedPreferences(itemView.context)
            val tokenAuth = itemView.context.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
            val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()

            binding.apply {
                tvSmNomorAgenda.text = item.nomer_agenda
                tvSmPenerima.text = itemView.context.getString(R.string.dispo_penerima, item.penerima)
                tvSmBentuk.text = itemView.context.getString(R.string.sm_bentuk, item.bentuk)
                tvSmSumber.text = itemView.context.getString(R.string.sm_sumber, item.sumber)
                tvSmDibuat.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.tanggal_surat)
                tvSmStatus.text = itemView.context.getString(R.string.cuti_status_view, item.status_surat)

                btnSmTeruskan.setOnClickListener {
                    showBottomSheet(iduser, item.idsurat_masuk, "terusan", tokenAuth)
                }

                if (item.status_surat == "MASUK") {
                    btnSmDisposisi.visibility = View.VISIBLE
                    btnSmDisposisi.setOnClickListener {
                        showBottomSheet(iduser, item.idsurat_masuk, "disposisi", tokenAuth)
                    }
                } else {
                    btnSmDisposisi.visibility = View.GONE
                }

                btnSmImg.setOnClickListener {
                    bottomSheetGambarSuratBinding = BottomSheetGambarSuratBinding.inflate(LayoutInflater.from(itemView.context))
                    val dialog = BottomSheetDialog(itemView.context).apply {
                        setCancelable(true)
                        setContentView(bottomSheetGambarSuratBinding.root)
                    }

                    val imgList = ArrayList<SlideModel>()
                    item.img?.let {
                        for (img in it) {
                            imgList.add(SlideModel(img.img))
                        }
                    } ?: imgList.add(SlideModel(R.raw.no_images))

                    bottomSheetGambarSuratBinding.imgsliderGiat.setImageList(imgList)
                    dialog.show()
                }

                if (item.riwayat.toInt() != 0) {
                    btnSmRiwayat.visibility = View.VISIBLE
                    btnSmRiwayat.setOnClickListener {
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
                                llChild.addView(createTextView(itemView.context.getString(R.string.dispo_pengirim, data.pengirim), 16.0F))
                                llChild.addView(createTextView(itemView.context.getString(R.string.dispo_penerima, data.penerima), 16.0F))
                                if (data.catatan.isNotEmpty()) {
                                    llChild.addView(createTextView(itemView.context.getString(R.string.dispo_catatan, data.catatan), 14.0F))
                                }
                                if (data.catatan_tambahan.isNotEmpty()) {
                                    llChild.addView(createTextView(itemView.context.getString(R.string.dispo_catatan_tambahan, data.catatan_tambahan), 14.0F))
                                }
                                llChild.addView(separator)

                                linearlayout.addView(llChild)
                            }
                        }

                        dialog.show()
                    }
                } else {
                    btnSmRiwayat.visibility = View.GONE
                }
            }
        }

        private fun showBottomSheet(iduser: String, idsurat: String, jenis: String, tokenAuth: String) {
            bottomSheetDisposisiSuratBinding = BottomSheetDisposisiSuratBinding.inflate(LayoutInflater.from(itemView.context))
            val dialog = BottomSheetDialog(itemView.context).apply {
                setCancelable(true)
                setContentView(bottomSheetDisposisiSuratBinding.root)
                behavior.peekHeight = 1500
            }

            bottomSheetDisposisiSuratBinding.apply {
                if (jenis == "terusan") {
                    catatanTambahanDisposisiCheckbox.visibility = View.GONE
                    tvCatatanTambahan.visibility = View.GONE
                    inputDisposisiCatatan.hint = "Catatan"
                } else {
                    inputDisposisiCatatan.hint = "Disposisi"
                }

                val listUser = ArrayList<String>()
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

                val checkboxList = listOf(
                    checkboxAccCatat,
                    checkboxIngatkan,
                    checkboxKoordinasikan,
                    checkboxLapor,
                    checkboxMonitor,
                    checkboxPedomani,
                    checkboxSarankan,
                    checkboxStSprinkanEdarkan,
                    checkboxTindakLanjuti,
                    checkboxUdkArsipkanMonitor,
                    checkboxUdl,
                    checkboxWakili
                )
                val checkedList = mutableListOf<String>()
                for (checkbox in checkboxList) {
                    checkbox.setOnCheckedChangeListener { _, isChecked ->
                        val checkBoxData = checkbox.text.toString().replace(" ", "_").lowercase(Locale.ROOT)
                        if (isChecked) {
                            checkedList.add(checkBoxData)
                        } else {
                            checkedList.remove(checkBoxData)
                        }
                        catatanTambahan = checkedList.joinToString(",")
                    }
                }
                // join list to string with comma

                btnSendDisposisi.setOnClickListener {
                    btnSendDisposisi.startAnimation()
                    Log.e("iduser", catatanTambahan)
                    if (idPenerima != "" && catatanTambahan != "") {
                        val catatan = inputDisposisiCatatan.text.toString()
                        insertSuratDisposisi(iduser, idsurat, jenis, catatan, catatanTambahan, idPenerima, tokenAuth)
                        idPenerima = ""
                        catatanTambahan = ""
                        dialog.dismiss()
                    } else {
                        btnSendDisposisi.endAnimation()
                        val errorMessage = if (jenis == "teruskan") {
                            "Penerima Terusan Tidak Boleh Kosong"
                        } else {
                            "Penerima Disposisi Tidak Boleh Kosong"
                        }
                        Toasty.warning(itemView.context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

            dialog.show()
        }

        private fun insertSuratDisposisi(iduser: String, idsurat: String, jenis: String, catatan: String, catatanTambahan: String, penerima: String, tokenAuth: String) {
            val service = RetrofitClient().apiRequest().create(SuratService::class.java)
            service.insertSuratDisposisi(iduser, idsurat, "surat_masuk", jenis, catatan, catatanTambahan, penerima, tokenAuth)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                Toasty.success(itemView.context, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                itemView.context.startActivity(Intent(itemView.context, SuratMasukActivity::class.java))
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
        val itemBinding = ItemSuratMasukBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}