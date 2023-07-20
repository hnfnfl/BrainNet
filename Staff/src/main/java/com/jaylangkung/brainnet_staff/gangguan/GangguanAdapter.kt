package com.jaylangkung.brainnet_staff.gangguan

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.data_class.GangguanEntity
import com.jaylangkung.brainnet_staff.databinding.ItemGangguanBinding

class GangguanAdapter : RecyclerView.Adapter<GangguanAdapter.ItemHolder>() {

    private var list = ArrayList<GangguanEntity>()

    fun setItem(item: List<GangguanEntity>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemRangeChanged(0, item.size)
    }

    class ItemHolder(private val binding: ItemGangguanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(gangguanList: GangguanEntity) {
            with(binding) {
                tvTime.text = gangguanList.tanggal
                tvTicket.text = gangguanList.nomer_tiket
                tvName.text = itemView.context.getString(R.string.gangguan_name, gangguanList.nama)
                tvAddress.text = itemView.context.getString(R.string.gangguan_address, gangguanList.alamat)
                tvToWho.text = itemView.context.getString(R.string.gangguan_to, gangguanList.kepada)
                tvDetail.text = itemView.context.getString(R.string.gangguan_detail, gangguanList.isi)
                tvStatus.text = gangguanList.status
                tvPriority.text = gangguanList.prioritas
                if (gangguanList.status == "open") {
                    tvStatus.typeface = Typeface.DEFAULT_BOLD
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_blue_400))
                } else {
                    tvStatus.typeface = Typeface.DEFAULT
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }

                when (gangguanList.prioritas) {
                    "low" -> {
                        parentLayout.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_blue_100))
                        tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_blue_400))
                    }
                    "medium" -> {
                        parentLayout.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_orange_100))
                        tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_orange_400))
                    }
                    "high" -> {
                        parentLayout.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_red_100))
                        tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_red_400))
                    }
                    "urgent" -> {
                        parentLayout.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.md_red_A100))
                        tvPriority.typeface = Typeface.DEFAULT_BOLD
                        tvPriority.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_red_A700))
                    }
                }

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, GangguanDetailActivity::class.java)
                        .apply {
                            putExtra(GangguanDetailActivity.idgangguan, gangguanList.idgangguan)
                        }
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemGangguanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

}



