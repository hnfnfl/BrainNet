package com.jaylangkung.korem.surat.masuk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.SuratMasukData
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

        fun bind(item: SuratMasukData) {
            binding.apply {
                tvSmNomorAgenda.text = item.nomer_agenda
                tvSmPenerima.text = itemView.context.getString(R.string.sm_penerima, item.penerima)
                tvSmBentuk.text = itemView.context.getString(R.string.sm_bentuk, item.bentuk)
                tvSmSumber.text = itemView.context.getString(R.string.sm_sumber, item.sumber)
                tvSmDibuat.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.tanggal_surat)
                tvSmStatus.text = itemView.context.getString(R.string.cuti_status_view, item.status_surat)
                btnSmRiwayat.visibility = if (item.riwayat.toInt() != 0) View.VISIBLE else View.GONE
                btnSmDisposisi.visibility = if (item.status_surat == "masuk") View.VISIBLE else View.GONE
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