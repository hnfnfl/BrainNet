package com.jaylangkung.brainnet_staff.todo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.data_class.TodoEntity
import com.jaylangkung.brainnet_staff.databinding.ItemTodoListBinding

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ItemHolder>() {

    private var list = ArrayList<TodoEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setItem(todoItem: List<TodoEntity>?) {
        if (todoItem == null) return
        list.clear()
        list.addAll(todoItem)
        notifyItemRangeChanged(0, list.size)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<TodoEntity>, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ItemHolder(private val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoItem: TodoEntity) {
            with(binding) {
                tvTodoList.text = todoItem.todo
                tvCreator.text = todoItem.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
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



