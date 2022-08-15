package com.jaylangkung.indirisma.menu_pelayanan.pemasangan_selesai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.databinding.ItemPemasanganSelesaiBinding
import com.jaylangkung.indirisma.menu_pelanggan.spinnerData.DataSpinnerEntity

class PemasanganSelesaiAdapter : RecyclerView.Adapter<PemasanganSelesaiAdapter.ItemHolder>() {

    private var list = ArrayList<DataSpinnerEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setItem(item: List<DataSpinnerEntity>?) {
        if (item == null) return
        list.clear()
        list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<DataSpinnerEntity>, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ItemHolder(private val binding: ItemPemasanganSelesaiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataSpinnerEntity) {
            with(binding) {
                tvName.text = item.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemPemasanganSelesaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val vendorItem = list[position]
        holder.bind(vendorItem)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(list, position)
        }
    }

    override fun getItemCount(): Int = list.size
}



