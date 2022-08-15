package com.jaylangkung.indirisma.todo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.indirisma.databinding.ItemTodoListBinding

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ItemHolder>() {

    private var list = ArrayList<TodoEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setItem(item: List<TodoEntity>?) {
        if (item == null) return
        list.clear()
        list.addAll(item)
        notifyItemRangeChanged(0, list.size)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<TodoEntity>, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ItemHolder(private val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoEntity) {
            with(binding) {
                tvTodoList.text = item.todo
                tvCreator.text = item.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(list, position)
        }
    }

    override fun getItemCount(): Int = list.size
}



