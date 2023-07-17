package com.jaylangkung.eoffice_korem.surat.keluar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.eoffice_korem.R
import com.jaylangkung.eoffice_korem.dataClass.DefaultResponse
import com.jaylangkung.eoffice_korem.dataClass.SuratKeluarData
import com.jaylangkung.eoffice_korem.databinding.ItemSuratKeluarBinding
import com.jaylangkung.eoffice_korem.retrofit.RetrofitClient
import com.jaylangkung.eoffice_korem.retrofit.SuratService
import com.jaylangkung.eoffice_korem.surat.DisposisiActivity
import com.jaylangkung.eoffice_korem.surat.showDisposisiRiwayat
import com.jaylangkung.eoffice_korem.utils.showFilesSurat
import com.jaylangkung.eoffice_korem.utils.Constants
import com.jaylangkung.eoffice_korem.utils.ErrorHandler
import com.jaylangkung.eoffice_korem.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

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
        private lateinit var myPreferences: MySharedPreferences

        fun bind(item: SuratKeluarData) {
            myPreferences = MySharedPreferences(itemView.context)
            val tokenAuth = itemView.context.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
            val iduser = myPreferences.getValue(Constants.USER_IDAKSES_SURAT).toString()

            binding.apply {
                tvSkNomorAgenda.text = item.nomer_agenda
                tvSkPenerima.text = itemView.context.getString(R.string.dispo_penerima, item.penerima)
                tvSkPerihal.text = itemView.context.getString(R.string.sk_perihal, item.perihal)
                tvSkDibuat.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.tanggal_surat)
                tvSkStatus.text = itemView.context.getString(R.string.cuti_status_view, item.status_surat_keluar)

                btnSkTeruskan.setOnClickListener {
                    itemView.context.startActivity(
                        Intent(itemView.context, DisposisiActivity::class.java).putExtra("caller", "surat_keluar").putExtra("jenis", "terusan")
                            .putExtra("idsurat", item.idsurat_keluar)
                    )
                }

                when (item.status_surat_keluar) {
                    "DRAFT" -> {
                        btnSkAjukan.visibility = View.VISIBLE
                        btnSkAcc.visibility = View.GONE
                        btnSkAjukan.setOnClickListener {
                            editSuratKeluar(item.idsurat_keluar, "pengajuan", tokenAuth)
                        }
                    }
                    "PENGAJUAN" -> {
                        btnSkAjukan.visibility = View.GONE
                        if (iduser == item.tanda_tangan) {
                            btnSkAcc.visibility = View.VISIBLE
                            btnSkAcc.setOnClickListener {
                                editSuratKeluar(item.idsurat_keluar, "diacc", tokenAuth)
                            }
                        } else {
                            btnSkAcc.visibility = View.GONE
                        }
                    }
                    else -> {
                        btnSkAjukan.visibility = View.GONE
                        btnSkAcc.visibility = View.GONE
                    }
                }

                btnSkImg.setOnClickListener {
                    showFilesSurat(itemView.context, item.img, item.pdf)
                }

                if (item.riwayat.toInt() != 0) {
                    btnSkRiwayat.visibility = View.VISIBLE
                    btnSkRiwayat.setOnClickListener {
                        showDisposisiRiwayat(itemView.context, item.riwayat_disposisi)
                    }
                } else {
                    btnSkRiwayat.visibility = View.GONE
                }
            }
        }

        private fun editSuratKeluar(idsurat: String, status: String, tokenAuth: String) {
            val service = RetrofitClient().apiRequest().create(SuratService::class.java)
            service.editSuratKeluar(idsurat, "", "", "", "", status, tokenAuth).enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                Toasty.success(itemView.context, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                itemView.context.startActivity(Intent(itemView.context, SuratKeluarActivity::class.java))
                                (itemView.context as Activity).finish()
                            }
                        } else {
                            ErrorHandler().responseHandler(
                                itemView.context, "insertSuratDisposisi | onResponse", response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        ErrorHandler().responseHandler(
                            itemView.context, "insertSuratDisposisi | onFailure", t.message.toString()
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