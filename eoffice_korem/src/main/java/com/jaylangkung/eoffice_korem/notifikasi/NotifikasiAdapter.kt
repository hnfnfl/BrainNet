package com.jaylangkung.eoffice_korem.notifikasi

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.eoffice_korem.dataClass.NotifikasiData
import com.jaylangkung.eoffice_korem.databinding.ItemNotifikasiBinding

class NotifikasiAdapter : RecyclerView.Adapter<NotifikasiAdapter.NotifItemHolder>() {

    private var list = ArrayList<NotifikasiData>()

    fun setItem(item: List<NotifikasiData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItem() {
        this.list.clear()
        notifyDataSetChanged()
    }

    class NotifItemHolder(private val binding: ItemNotifikasiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotifikasiData) {
            with(binding) {
                tvJudul.text = item.judul
                tvIsi.text = item.notifikasi_user
                tvDate.text = item.waktu
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifItemHolder {
        val itemNotifBinding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifItemHolder(itemNotifBinding)
    }

    override fun onBindViewHolder(holder: NotifItemHolder, position: Int) {
        val vendorItem = list[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = list.size
}



