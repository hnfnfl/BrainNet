package com.jaylangkung.indirisma.notifikasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.databinding.ItemNotifikasiBinding

class NotifikasiAdapter : RecyclerView.Adapter<NotifikasiAdapter.ItemHolder>() {

    private var list = ArrayList<NotifikasiEntity>()

    fun setItem(item: List<NotifikasiEntity>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemNotifikasiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotifikasiEntity) {
            with(binding) {
                tvJenis.text = item.jenis
                tvIsi.text = item.isi
                tvDate.text = item.waktu
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}



