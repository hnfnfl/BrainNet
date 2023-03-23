package com.jaylangkung.korem.siapOpsGerak

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.R
import com.jaylangkung.korem.dataClass.SiapOpsGerakData
import com.jaylangkung.korem.databinding.ItemSiapOpsGerakBinding
import com.jaylangkung.korem.survey.SurveyActivity.Companion.jawabanList


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

                val rgParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                rgParams.setMargins(0, 24, 0, 0)
                RadioGroup(itemView.context).apply {
                    layoutParams = rgParams

                    for (data in item.kendaraan) {
//                        val rbParams = LinearLayout.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT,
//                        )
//                        rbParams.setMargins(0, 12, 0, 0)
//                        RadioButton(itemView.context).apply {
//                            layoutParams = rbParams
//                            text = data.value
//                            textSize = 18F
//                            id = data.id
//                            addView(this)
//                        }
                    }
                    llPertanyaan.addView(this)

                    setOnCheckedChangeListener { _, _ ->
                        val selectedId: Int = this.checkedRadioButtonId
                        val radioButton = findViewById<RadioButton>(selectedId)
                        jawabanList[item.idsurvey.toInt()] = radioButton.text.toString()
                    }
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