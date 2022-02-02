package com.jaylangkung.brainnet_staff.todo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.databinding.ItemTodoListBinding

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoItemHolder>() {

    private var listTodo = ArrayList<TodoEntity>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setTodoItem(todoItem: List<TodoEntity>?) {
        if (todoItem == null) return
        listTodo.clear()
        listTodo.addAll(todoItem)
        notifyItemRangeChanged(0, listTodo.size)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ArrayList<TodoEntity>, position: Int)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class TodoItemHolder(private val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoItem: TodoEntity) {
            with(binding) {
                tvTodoList.text = todoItem.todo
                tvCreator.text = todoItem.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemHolder {
        val itemTodoBinding = ItemTodoListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoItemHolder(itemTodoBinding)
    }

    override fun onBindViewHolder(holder: TodoItemHolder, position: Int) {
        val vendorItem = listTodo[position]
        holder.bind(vendorItem)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listTodo, position)
        }
    }

    override fun getItemCount(): Int = listTodo.size
}



