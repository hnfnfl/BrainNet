package com.jaylangkung.indirisma.gangguan

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ItemGangguanBinding

class GangguanAdapter : RecyclerView.Adapter<GangguanAdapter.ItemHolder>() {

    private var list = ArrayList<GangguanEntity>()

    fun setItem(item: List<GangguanEntity>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemRangeChanged(0, item.size)
    }

    class ItemHolder(private val binding: ItemGangguanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GangguanEntity) {
            with(binding) {
                tvTime.text = item.tanggal
                tvTicket.text = item.nomer_tiket
                tvName.text = itemView.context.getString(R.string.gangguan_name, item.nama)
                tvAddress.text = itemView.context.getString(R.string.gangguan_address, item.alamat)
                tvToWho.text = itemView.context.getString(R.string.gangguan_to, item.kepada)
                tvDetail.text = itemView.context.getString(R.string.gangguan_detail, item.isi)
                tvStatus.text = item.status
                tvPriority.text = item.prioritas
                if (item.status == "open") {
                    tvStatus.typeface = Typeface.DEFAULT_BOLD
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.md_blue_400))
                } else {
                    tvStatus.typeface = Typeface.DEFAULT
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }

                when (item.prioritas) {
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
                            putExtra(GangguanDetailActivity.idgangguan, item.idgangguan)
                        }
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemGangguanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size

}



