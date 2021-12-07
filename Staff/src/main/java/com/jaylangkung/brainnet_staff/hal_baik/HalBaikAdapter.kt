package com.jaylangkung.brainnet_staff.hal_baik

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.databinding.ItemHalBaikBinding
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import kotlin.collections.ArrayList

class HalBaikAdapter : RecyclerView.Adapter<HalBaikAdapter.HalBaikItemHolder>() {

    private var listHalBaik = ArrayList<HalBaikEntity>()

    fun setHalBaikItem(userItem: List<HalBaikEntity>?) {
        if (userItem == null) return
        this.listHalBaik.clear()
        this.listHalBaik.addAll(userItem)
        notifyDataSetChanged()
    }

    class HalBaikItemHolder(private val binding: ItemHalBaikBinding) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var myPreferences: MySharedPreferences

        fun bind(halBaikItem: HalBaikEntity) {
            with(binding) {
                myPreferences = MySharedPreferences(itemView.context)

                tvDate.text = halBaikItem.tanggal
                tvHalBaik.text = halBaikItem.hal_baik
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HalBaikItemHolder {
        val itemHalBaikBinding = ItemHalBaikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HalBaikItemHolder(itemHalBaikBinding)
    }

    override fun onBindViewHolder(holder: HalBaikItemHolder, position: Int) {
        val vendorItem = listHalBaik[position]
        holder.bind(vendorItem)
    }

    override fun getItemCount(): Int {
        return listHalBaik.size
    }
}



