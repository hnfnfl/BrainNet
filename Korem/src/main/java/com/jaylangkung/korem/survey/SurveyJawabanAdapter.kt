package com.jaylangkung.korem.survey

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.korem.dataClass.SurveyJawaban
import com.jaylangkung.korem.databinding.ItemJawabanSurveyBinding
import com.jaylangkung.korem.utils.MySharedPreferences


class SurveyJawabanAdapter : RecyclerView.Adapter<SurveyJawabanAdapter.ItemHolder>() {

    private var list = ArrayList<SurveyJawaban>()

    fun setItem(item: List<SurveyJawaban>?) {
        if (item == null) return
        this.list.clear()
        this.list.addAll(item)
        notifyItemChanged(0, list.size)
    }

    class ItemHolder(private val binding: ItemJawabanSurveyBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var myPreferences: MySharedPreferences

        fun bind(item: SurveyJawaban) {
            myPreferences = MySharedPreferences(itemView.context)
            binding.apply {

                rbJawaban.text = item.value
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