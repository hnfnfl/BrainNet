package com.jaylangkung.brainnet_staff.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.EthernetEntity
import com.jaylangkung.brainnet_staff.databinding.ItemEthernetBinding

class EthernetAdapter : RecyclerView.Adapter<EthernetAdapter.EthernetItemHolder>() {

    private var listEthernet = ArrayList<EthernetEntity>()

    fun setEthernetItem(todoItem: List<EthernetEntity>?) {
        if (todoItem == null) return
        listEthernet.clear()
        listEthernet.addAll(todoItem)
        notifyItemRangeChanged(0, listEthernet.size)
    }

    class EthernetItemHolder(private val binding: ItemEthernetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ethernetItem: EthernetEntity) {
            with(binding) {
                tvEthernet.text = ethernetItem.name
                if (ethernetItem.status == "false") {
                    cvEthernet.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_red_A700))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EthernetItemHolder {
        val itemEthernetBinding = ItemEthernetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EthernetItemHolder(itemEthernetBinding)
    }

    override fun onBindViewHolder(holder: EthernetItemHolder, position: Int) {
        val vendorItem = listEthernet[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = listEthernet.size
}



