package com.jaylangkung.brainnet_staff.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ItemUserDcBinding

class UserDCAdapter : RecyclerView.Adapter<UserDCAdapter.UserDCItemHolder>() {

    private var listUserDC = ArrayList<UserDCEntity>()

    fun setUserDCItem(todoItem: List<UserDCEntity>?) {
        if (todoItem == null) return
        listUserDC.clear()
        listUserDC.addAll(todoItem)
        notifyDataSetChanged()
    }

    class UserDCItemHolder(private val binding: ItemUserDcBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDCItemHolder {
        val itemUserDCBinding = ItemUserDcBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserDCItemHolder(itemUserDCBinding)
    }

    override fun onBindViewHolder(holder: UserDCItemHolder, position: Int) {
        val vendorItem = listUserDC[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int = listUserDC.size
}



