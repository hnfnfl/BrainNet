package com.jaylangkung.indirisma.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ItemEthernetBinding

class EthernetAdapter : RecyclerView.Adapter<EthernetAdapter.ItemHolder>() {

    private var list = ArrayList<EthernetEntity>()

    fun setItem(item: List<EthernetEntity>?) {
        if (item == null) return
        list.clear()
        list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemEthernetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EthernetEntity) {
            with(binding) {
                tvEthernet.text = item.name
                if (item.status == "false") {
                    cvEthernet.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_red_A700))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemEthernetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val vendorItem = list[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = list.size
}



