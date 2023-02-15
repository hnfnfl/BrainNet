package com.jaylangkung.korem.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.dataClass.SurveyData
import com.jaylangkung.korem.databinding.ItemPertanyaanSurveyBinding
import com.jaylangkung.korem.survey.SurveyActivity.Companion.jawabanList
import com.jaylangkung.korem.utils.MySharedPreferences


class SurveyPertanyaanAdapter : RecyclerView.Adapter<SurveyPertanyaanAdapter.ItemHolder>() {

    private var list = ArrayList<SurveyData>()

    fun setItem(item: List<SurveyData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemPertanyaanSurveyBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var myPreferences: MySharedPreferences
        fun bind(item: SurveyData) {
            myPreferences = MySharedPreferences(itemView.context)

            binding.apply {
                tvPertanyaan.text = item.pertanyaan

                val rgParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                rgParams.setMargins(0, 24, 0, 0)
                RadioGroup(itemView.context).apply {
                    layoutParams = rgParams

                    for (data in item.jawaban) {
                        val rbParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                        rbParams.setMargins(0, 12, 0, 0)
                        RadioButton(itemView.context).apply {
                            layoutParams = rbParams
                            text = data.value
                            textSize = 18F
                            id = data.id
                            addView(this)
                        }
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
        val itemBinding = ItemPertanyaanSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}