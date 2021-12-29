package com.jaylangkung.brainnet_staff.todo_list

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ItemTodoListBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoItemHolder>() {

    companion object {
        private var listTodo = ArrayList<TodoEntity>()
    }

    fun setTodoItem(todoItem: List<TodoEntity>?) {
        if (todoItem == null) return
        listTodo.clear()
        listTodo.addAll(todoItem)
        notifyDataSetChanged()
    }

    class TodoItemHolder(private val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var myPreferences: MySharedPreferences
        fun bind(todoItem: TodoEntity) {
            with(binding) {
                myPreferences = MySharedPreferences(itemView.context)

                val tokenAuth = itemView.context.getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
                val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
                val idtodoList = todoItem.idtodo_list

                tvTodoList.text = todoItem.todo
                tvCreator.text = todoItem.nama

                tvTodoList.setOnClickListener {
                    Log.e("listTodo", listTodo[0].toString())
                    val mDialog = MaterialDialog.Builder(itemView.context as Activity)
                        .setTitle("Selesaikan ToDo")
                        .setMessage("Apakah Anda yakin ingin menyelesaikan ToDo ini?")
                        .setCancelable(true)
                        .setPositiveButton(itemView.context.getString(R.string.yes), R.drawable.ic_restart)
                        { dialogInterface, _ ->
                            val service = RetrofitClient().apiRequest().create(DataService::class.java)
                            service.editTodo(idtodoList, idadmin, tokenAuth).enqueue(object : Callback<DefaultResponse> {
                                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                                    if (response.isSuccessful) {
                                        if (response.body()!!.status == "success") {
                                            Toasty.success(itemView.context, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                    Toasty.error(itemView.context, t.message.toString(), Toasty.LENGTH_LONG).show()
                                }
                            })
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton(itemView.context.getString(R.string.no), R.drawable.ic_close)
                        { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        .build()
                    // Show Dialog
                    mDialog.show()
                }
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

    override fun getItemCount(): Int = listTodo.size
}



