package com.jaylangkung.brainnet_staff.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.UserDCEntity
import com.jaylangkung.brainnet_staff.databinding.ItemUserDcBinding

class UserDCAdapter : RecyclerView.Adapter<UserDCAdapter.ItemHolder>() {

    private var list = ArrayList<UserDCEntity>()

    fun setItem(todoItem: List<UserDCEntity>?) {
        if (todoItem == null) return
        list.clear()
        list.addAll(todoItem)
        notifyItemRangeChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemUserDcBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userDCItem: UserDCEntity) {
            with(binding) {
                tvName.text = userDCItem.nama
                tvUsername.text = userDCItem.user
                tvPaketPhone.text = itemView.context.getString(R.string.paket_phone, userDCItem.paket, userDCItem.nohp)
                tvAddress.text = userDCItem.alamat_pasang
                tvCable.text = itemView.context.getString(R.string.kabel, userDCItem.jenis_kabel, userDCItem.panjangkabel)
                tvSwitch.text = itemView.context.getString(R.string.nomer_switch, userDCItem.nomer_switch)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemUserDcBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}



