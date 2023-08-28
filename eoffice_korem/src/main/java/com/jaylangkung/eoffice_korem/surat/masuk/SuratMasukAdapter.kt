package com.jaylangkung.eoffice_korem.surat.masuk

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.SuratMasukData
import com.jaylangkung.eoffice_korem.databinding.ItemSuratMasukBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.surat.DisposisiActivity
import com.jaylangkung.eoffice_korem.surat.showDisposisiRiwayat
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import com.jaylangkung.eoffice_korem.utils.showFilesSurat
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
        private lateinit var myPreferences: MySharedPreferences

        fun bind(item: SuratMasukData) {
            myPreferences = MySharedPreferences(itemView.context)
            val tokenAuth = itemView.context.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())

            binding.apply {
                tvSmNomorAgenda.text = item.nomerAgenda
                tvSmPenerima.text = itemView.context.getString(R.string.dispo_penerima, item.penerima)
                tvSmBentuk.text = itemView.context.getString(R.string.sm_bentuk, item.bentuk)
                tvSmSumber.text = itemView.context.getString(R.string.sm_sumber, item.sumber)
                tvSmDibuat.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.tanggalSurat)
                tvSmStatus.text = itemView.context.getString(R.string.cuti_status_view, item.statusSurat)

                btnSmTeruskan.setOnClickListener {
                    itemView.context.startActivity(
                        Intent(itemView.context, DisposisiActivity::class.java)
                            .putExtra("caller", "surat_masuk")
                            .putExtra("jenis", "terusan")
                            .putExtra("idsurat", item.idsuratMasuk)
                    )
                }

                if (item.statusSurat == "MASUK") {
                    btnSmDisposisi.visibility = View.VISIBLE
                    btnSmDisposisi.setOnClickListener {
                        itemView.context.startActivity(
                            Intent(itemView.context, DisposisiActivity::class.java)
                                .putExtra("caller", "surat_masuk")
                                .putExtra("jenis", "disposisi")
                                .putExtra("idsurat", item.idsuratMasuk)
                        )
                    }
                } else {
                    btnSmDisposisi.visibility = View.GONE
                }

                btnSmImg.setOnClickListener {
                    showFilesSurat(itemView.context, item.img, null)
                    if (item.statusSurat == "MASUK") {
                        editSuratMasuk(item.idsuratMasuk, tokenAuth)
                        tvSmStatus.text = itemView.context.getString(R.string.cuti_status_view, "DIBACA")
                    }
                }

                if (item.riwayat.toInt() != 0) {
                    btnSmRiwayat.visibility = View.VISIBLE
                    btnSmRiwayat.setOnClickListener {
                        showDisposisiRiwayat(itemView.context, item.riwayatDisposisi)
                    }
                } else {
                    btnSmRiwayat.visibility = View.GONE
                }
            }
        }

        private fun editSuratMasuk(id: String, tokenAuth: String) {
            val service = RetrofitClient().apiRequest().create(SuratService::class.java)
            service.editSuratMasuk(id, "", "", "", "", "dibaca", "", tokenAuth)
                .enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                Log.e("editSuratMasuk", response.body()!!.message)
                            }
                        } else {
                            ErrorHandler().responseHandler(
                                itemView.context, "editSuratMasuk | onResponse", response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        ErrorHandler().responseHandler(
                            itemView.context, "editSuratMasuk | onFailure", t.message.toString()
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