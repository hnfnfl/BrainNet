package com.jaylangkung.indirisma.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ItemUserDcBinding

class UserDCAdapter : RecyclerView.Adapter<UserDCAdapter.ItemHolder>() {

    private var list = ArrayList<UserDCEntity>()

    fun setItem(item: List<UserDCEntity>?) {
        if (item == null) return
        list.clear()
        list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemUserDcBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDCEntity) {
            with(binding) {
                tvName.text = item.nama
                tvUsername.text = item.user
                tvPaketPhone.text = itemView.context.getString(R.string.paket_phone, item.paket, item.nohp)
                tvAddress.text = item.alamat_pasang
                tvCable.text = itemView.context.getString(R.string.kabel, item.jenis_kabel, item.panjangkabel)
                tvSwitch.text = itemView.context.getString(R.string.nomer_switch, item.nomer_switch)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemUserDcBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}



