package com.jaylangkung.korem.notifikasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.dataClass.NotifikasiData
import com.jaylangkung.korem.databinding.ItemNotifikasiBinding

class NotifikasiAdapter : RecyclerView.Adapter<NotifikasiAdapter.NotifItemHolder>() {

    private var listNotifikasi = ArrayList<NotifikasiData>()

    fun setNotifItem(notifItem: List<NotifikasiData>?) {
        if (notifItem == null) return
        this.listNotifikasi.clear()
        this.listNotifikasi.addAll(notifItem)
        notifyItemRangeChanged(0, listNotifikasi.size)
    }

    class NotifItemHolder(private val binding: ItemNotifikasiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notifItem: NotifikasiData) {
            with(binding) {
                tvJenis.text = notifItem.jenis
                tvIsi.text = notifItem.isi
                tvDate.text = notifItem.waktu
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifItemHolder {
        val itemNotifBinding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifItemHolder(itemNotifBinding)
    }

    override fun onBindViewHolder(holder: NotifItemHolder, position: Int) {
        val vendorItem = listNotifikasi[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = listNotifikasi.size
}



