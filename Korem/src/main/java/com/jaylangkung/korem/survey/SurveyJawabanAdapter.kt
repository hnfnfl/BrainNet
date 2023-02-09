package com.jaylangkung.korem.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.dataClass.SurveyData
import com.jaylangkung.korem.databinding.ItemJawabanSurveyBinding
import com.jaylangkung.korem.databinding.ItemPertanyaanSurveyBinding
import com.jaylangkung.korem.utils.MySharedPreferences

class SurveyJawabanAdapter : RecyclerView.Adapter<SurveyJawabanAdapter.ItemHolder>() {

    private var list = ArrayList<SurveyData>()

    fun setItem(item: List<SurveyData>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemJawabanSurveyBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var myPreferences: MySharedPreferences

        fun bind(item: SurveyData) {
            myPreferences = MySharedPreferences(itemView.context)
            binding.apply {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemJawabanSurveyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = list.size
}