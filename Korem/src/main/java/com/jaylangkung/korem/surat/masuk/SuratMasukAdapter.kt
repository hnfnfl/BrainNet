package com.jaylangkung.korem.surat.masuk

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.SuratMasukData
import com.jaylangkung.korem.databinding.BottomSheetRiwayatDisposisiBinding
import com.jaylangkung.korem.databinding.ItemSuratMasukBinding

class SuratMasukAdapter : RecyclerView.Adapter<SuratMasukAdapter.ItemHolder>() {

    private var list = ArrayList<SuratMasukData>()
    fun setItem(item: List<SuratMasukData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemSuratMasukBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var bottomSheetRiwayatDisposisiBinding: BottomSheetRiwayatDisposisiBinding

        fun bind(item: SuratMasukData) {
            binding.apply {
                tvSmNomorAgenda.text = item.nomer_agenda
                tvSmPenerima.text = itemView.context.getString(R.string.sm_penerima, item.penerima)
                tvSmBentuk.text = itemView.context.getString(R.string.sm_bentuk, item.bentuk)
                tvSmSumber.text = itemView.context.getString(R.string.sm_sumber, item.sumber)
                tvSmDibuat.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.tanggal_surat)
                tvSmStatus.text = itemView.context.getString(R.string.cuti_status_view, item.status_surat)
                btnSmDisposisi.visibility = if (item.status_surat == "masuk") View.VISIBLE else View.GONE

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
                                    setMargins(0, 6, 0, 0)
                                }

                                val separator = View(itemView.context).apply {
                                    layoutParams = LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, 2
                                    ).apply { setMargins(0, 10, 0, 0) }
                                    setBackgroundColor(Color.parseColor("#c0c0c0"))
                                }

                                val noAgenda = TextView(itemView.context).apply {
                                    text = "${data.nomer_agenda} (${data.aksi})"
                                    setTextColor(itemView.context.getColor(R.color.black))
                                    textSize = 16.0F
                                    layoutParams = tvParams
                                }
                                val tglDisposisi = TextView(itemView.context).apply {
                                    text = data.tanggal_disposisi
                                    setTextColor(itemView.context.getColor(R.color.black))
                                    textSize = 16.0F
                                    layoutParams = tvParams
                                }
                                val pengirim = TextView(itemView.context).apply {
                                    text = itemView.context.getString(R.string.sm_pengirim, data.pengirim)
                                    setTextColor(itemView.context.getColor(R.color.black))
                                    textSize = 16.0F
                                    layoutParams = tvParams
                                }
                                val penerima = TextView(itemView.context).apply {
                                    text = itemView.context.getString(R.string.sm_penerima, data.penerima)
                                    setTextColor(itemView.context.getColor(R.color.black))
                                    textSize = 16.0F
                                    layoutParams = tvParams
                                }
                                val aksi = TextView(itemView.context).apply {
                                    text = itemView.context.getString(R.string.sm_aksi, data.aksi)
                                    setTextColor(itemView.context.getColor(R.color.black))
                                    textSize = 18.0F
                                    layoutParams = tvParams
                                }
                                llChild.addView(noAgenda)
                                llChild.addView(tglDisposisi)
                                llChild.addView(pengirim)
                                llChild.addView(penerima)
                                llChild.addView(aksi)
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