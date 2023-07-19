package com.jaylangkung.brainnet_staff.hal_baik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.data_class.HalBaikEntity
import com.jaylangkung.brainnet_staff.databinding.ItemHalBaikBinding

class HalBaikAdapter : RecyclerView.Adapter<HalBaikAdapter.HalBaikItemHolder>() {

    private var listHalBaik = ArrayList<HalBaikEntity>()

    fun setHalBaikItem(halBaikItem: List<HalBaikEntity>?) {
        if (halBaikItem == null) return
        this.listHalBaik.clear()
        this.listHalBaik.addAll(halBaikItem)
        notifyItemRangeChanged(0, listHalBaik.size)
    }

    class HalBaikItemHolder(private val binding: ItemHalBaikBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(halBaikItem: HalBaikEntity) {
            with(binding) {
                tvDate.text = halBaikItem.tanggal
                tvHalBaik.text = halBaikItem.hal_baik
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HalBaikItemHolder {
        val itemHalBaikBinding = ItemHalBaikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HalBaikItemHolder(itemHalBaikBinding)
    }

    override fun onBindViewHolder(holder: HalBaikItemHolder, position: Int) {
        val vendorItem = listHalBaik[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = listHalBaik.size
}



