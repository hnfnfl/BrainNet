package com.jaylangkung.brainnet_staff.notifikasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.data_class.NotifikasiEntity
import com.jaylangkung.brainnet_staff.databinding.ItemNotifikasiBinding

class NotifikasiAdapter : RecyclerView.Adapter<NotifikasiAdapter.ItemHolder>() {

    private var list = ArrayList<NotifikasiEntity>()

    fun setItem(notifItem: List<NotifikasiEntity>?) {
        if (notifItem == null) return
        this.list.clear()
        this.list.addAll(notifItem)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemNotifikasiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notifItem: NotifikasiEntity) {
            with(binding) {
                tvJenis.text = notifItem.jenis
                tvIsi.text = notifItem.isi
                tvDate.text = notifItem.waktu
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}



