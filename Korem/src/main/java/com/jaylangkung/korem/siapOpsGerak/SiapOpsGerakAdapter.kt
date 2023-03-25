package com.jaylangkung.korem.siapOpsGerak

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.SiapOpsGerakData
import com.jaylangkung.korem.databinding.ItemSiapOpsGerakBinding


class SiapOpsGerakAdapter : RecyclerView.Adapter<SiapOpsGerakAdapter.ItemHolder>() {

    private var list = ArrayList<SiapOpsGerakData>()

    fun setItem(item: List<SiapOpsGerakData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemSiapOpsGerakBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SiapOpsGerakData) {
            binding.apply {
                tvSiapKeterangan.text = itemView.context.getString(R.string.siap_keterangan_view, item.keterangan)
                tvSiapOps.text = itemView.context.getString(R.string.siap_ops_view, item.siap_ops)
                tvSiapGerak.text = itemView.context.getString(R.string.siap_gerak_view, item.siap_gerak)
                tvSiapCreateddate.text = item.createddate

                for (data in item.kendaraan) {
                    val textView = TextView(itemView.context).apply {
                        text = itemView.context.getString(R.string.siap_kendaraan_list, data.parameter, data.value)
                        setTextColor(itemView.context.getColor(R.color.black))
                        textSize = 16.0F
                    }
                    val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(24, 4, 0, 0)
                    textView.layoutParams = params

                    linearLayout.addView(textView)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemSiapOpsGerakBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}