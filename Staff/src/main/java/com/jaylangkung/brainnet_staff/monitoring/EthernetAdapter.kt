package com.jaylangkung.brainnet_staff.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.EthernetEntity
import com.jaylangkung.brainnet_staff.databinding.ItemEthernetBinding

class EthernetAdapter : RecyclerView.Adapter<EthernetAdapter.ItemHolder>() {

    private var list = ArrayList<EthernetEntity>()

    fun setItem(item: List<EthernetEntity>?) {
        if (item == null) return
        list.clear()
        list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemEthernetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ethernetItem: EthernetEntity) {
            with(binding) {
                tvEthernet.text = ethernetItem.name
                if (ethernetItem.status == "false") {
                    cvEthernet.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_red_A700))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemEthernetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}



