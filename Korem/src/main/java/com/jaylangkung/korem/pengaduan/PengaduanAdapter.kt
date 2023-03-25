package com.jaylangkung.korem.pengaduan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.PengaduanData
import com.jaylangkung.korem.databinding.ItemPengaduanBinding

class PengaduanAdapter : RecyclerView.Adapter<PengaduanAdapter.ItemHolder>() {

    private var list = ArrayList<PengaduanData>()

    fun setItem(item: List<PengaduanData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemPengaduanBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PengaduanData) {
            binding.apply {
                tvPengaduanJudul.text = itemView.context.getString(R.string.pengaduan_judul_view, item.judul)
                tvPengaduanBody.text = itemView.context.getString(R.string.pengaduan_isi_view, item.pengaduan)
                if (item.img != "") {
                    imgPengaduan.visibility = View.VISIBLE
                    Glide.with(itemView.context)
                        .load(item.img)
                        .apply(RequestOptions().override(650))
                        .error(R.drawable.ic_empty)
                        .into(imgPengaduan)
                }
                tvPengaduanCreate.text = itemView.context.getString(R.string.pengaduan_createddate_view, item.createddate)
                tvPengaduanUpdate.text = itemView.context.getString(R.string.pengaduan_lastupdate_view, item.lastupdate)
                tvPengaduanStatus.text = itemView.context.getString(R.string.cuti_status_view, item.status_pengaduan)
                if (item.oleh != "" && item.balasan != "") {
                    tvPengaduanOleh.visibility = View.VISIBLE
                    tvPengaduanBalasan.visibility = View.VISIBLE
                    tvPengaduanOleh.text = itemView.context.getString(R.string.pengaduan_oleh_view, item.oleh)
                    tvPengaduanBalasan.text = itemView.context.getString(R.string.pengaduan_balasan_view, item.balasan)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemPengaduanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}