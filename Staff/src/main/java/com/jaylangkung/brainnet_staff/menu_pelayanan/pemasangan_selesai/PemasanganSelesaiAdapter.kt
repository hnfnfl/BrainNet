package com.jaylangkung.brainnet_staff.menu_pelayanan.pemasangan_selesai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.databinding.ItemPemasanganSelesaiBinding
import com.jaylangkung.brainnet_staff.data_class.DataSpinnerEntity

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
        fun bind(pemasanganItem: DataSpinnerEntity) {
            with(binding) {
                tvName.text = pemasanganItem.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemPemasanganSelesaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(list, position)
        }
    }

    override fun getItemCount(): Int = list.size
}



