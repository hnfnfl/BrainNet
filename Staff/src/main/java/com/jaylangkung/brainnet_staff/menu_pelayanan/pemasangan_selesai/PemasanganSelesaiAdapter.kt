package com.jaylangkung.brainnet_staff.menu_pelayanan.pemasangan_selesai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.databinding.ItemPemasanganSelesaiBinding
import com.jaylangkung.brainnet_staff.data_class.DataSpinnerEntity

class PemasanganSelesaiAdapter : RecyclerView.Adapter<PemasanganSelesaiAdapter.PemasanganItemHolder>() {

    private var listPemasanganSelesai = ArrayList<DataSpinnerEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setPemasanganItem(pemasanganItem: List<DataSpinnerEntity>?) {
        if (pemasanganItem == null) return
        listPemasanganSelesai.clear()
        listPemasanganSelesai.addAll(pemasanganItem)
        notifyItemRangeChanged(0, listPemasanganSelesai.size)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<DataSpinnerEntity>, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class PemasanganItemHolder(private val binding: ItemPemasanganSelesaiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pemasanganItem: DataSpinnerEntity) {
            with(binding) {
                tvName.text = pemasanganItem.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PemasanganItemHolder {
        val itemPemasanganSelesaiBinding = ItemPemasanganSelesaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PemasanganItemHolder(itemPemasanganSelesaiBinding)
    }

    override fun onBindViewHolder(holder: PemasanganItemHolder, position: Int) {
        val vendorItem = listPemasanganSelesai[position]
        holder.bind(vendorItem)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listPemasanganSelesai, position)
        }
    }

    override fun getItemCount(): Int = listPemasanganSelesai.size
}



