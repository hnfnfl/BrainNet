package com.jaylangkung.korem.giat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.GiatData
import com.jaylangkung.korem.databinding.ItemGiatBinding

class GiatAdapter : RecyclerView.Adapter<GiatAdapter.ItemHolder>() {

    private var list = ArrayList<GiatData>()

    fun setItem(item: List<GiatData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemGiatBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GiatData) {
            binding.apply {
                tvGiatTujuan.text = itemView.context.getString(R.string.giat_tujuan_view, item.tujuan)
                tvGiatKeterangan.text = itemView.context.getString(R.string.keterangan_view, item.keterangan)
                tvGiatJenis.text = itemView.context.getString(R.string.jenis_view, item.jenis)
                tvGiatDepartemen.text = itemView.context.getString(R.string.giat_departemen_view, item.departemen)
                tvGiatLokasi.text = itemView.context.getString(R.string.giat_lokasi_view, item.lokasi)
                tvMulaiCuti.text = itemView.context.getString(R.string.mulai_view, item.mulai)
                tvSampaiCuti.text = itemView.context.getString(R.string.sampai_view, item.sampai)
                tvStatusGiat.text = itemView.context.getString(R.string.giat_proses_view, item.proses)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemGiatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}