package com.jaylangkung.indirisma.hal_baik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.databinding.ItemHalBaikBinding

class HalBaikAdapter : RecyclerView.Adapter<HalBaikAdapter.ItemHolder>() {

    private var list = ArrayList<HalBaikEntity>()

    fun setItem(item: List<HalBaikEntity>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemHalBaikBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HalBaikEntity) {
            with(binding) {
                tvDate.text = item.tanggal
                tvHalBaik.text = item.hal_baik
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemHalBaikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}



