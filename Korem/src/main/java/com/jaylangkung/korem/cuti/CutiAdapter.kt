package com.jaylangkung.korem.cuti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.CutiData
import com.jaylangkung.korem.databinding.ItemCutiBinding
import com.jaylangkung.korem.utils.MySharedPreferences

class CutiAdapter : RecyclerView.Adapter<CutiAdapter.ItemHolder>() {

    private var list = ArrayList<CutiData>()

    fun setItem(item: List<CutiData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemCutiBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var myPreferences: MySharedPreferences

        fun bind(item: CutiData) {
            myPreferences = MySharedPreferences(itemView.context)
            binding.apply {
                tvJudulCuti.text = itemView.context.getString(R.string.cuti_judul_view, item.cuti)
                tvKeteranganCuti.text = itemView.context.getString(R.string.cuti_keterangan_view, item.keterangan)
                tvJenisCuti.text = itemView.context.getString(R.string.cuti_jenis_view, item.jenis)
                tvMulaiCuti.text = itemView.context.getString(R.string.cuti_mulai_view, item.mulai)
                tvSampaiCuti.text = itemView.context.getString(R.string.cuti_sampai_view, item.sampai)
                tvStatusCuti.text = itemView.context.getString(R.string.cuti_status_view, item.status_cuti)
                if (item.revisi != "") {
                    tvRevisiCuti.visibility = View.VISIBLE
                    tvRevisiCuti.text = itemView.context.getString(R.string.cuti_revisi_view, item.revisi)
                }

                if (item.alasan_ditolak != "") {
                    tvDitolakCuti.visibility = View.VISIBLE
                    tvDitolakCuti.text = itemView.context.getString(R.string.cuti_ditolak_view, item.alasan_ditolak)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemCutiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}