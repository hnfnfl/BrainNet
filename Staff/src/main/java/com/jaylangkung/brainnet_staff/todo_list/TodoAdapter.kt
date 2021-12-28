package com.jaylangkung.brainnet_staff.todo_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.databinding.ItemTodoListBinding
import kotlin.collections.ArrayList

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoItemHolder>() {

    private var listTodo = ArrayList<TodoEntity>()

    fun setTodoItem(todoItem: List<TodoEntity>?) {
        if (todoItem == null) return
        this.listTodo.clear()
        this.listTodo.addAll(todoItem)
        notifyDataSetChanged()
    }

    class TodoItemHolder(private val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoItem: TodoEntity) {
            with(binding) {

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
    }

    override fun getItemCount(): Int {
        return listTodo.size
    }
}



