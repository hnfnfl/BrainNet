package com.jaylangkung.brainnet_staff.todo_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.databinding.ActivityToDoBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.TodoResponse
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TodoActivity : AppCompatActivity() {

    private lateinit var todoBinding: ActivityToDoBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var todoAdapter: TodoAdapter
    private var listTodo: ArrayList<TodoEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoBinding = ActivityToDoBinding.inflate(layoutInflater)
        setContentView(todoBinding.root)
        myPreferences = MySharedPreferences(this@TodoActivity)
        todoAdapter = TodoAdapter()

        todoBinding.btnBack.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onBackPressed() {
        startActivity(Intent(this@TodoActivity, MainActivity::class.java))
        finish()
    }

    private fun getTodo(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getTodo(tokenAuth).enqueue(object : Callback<TodoResponse> {
            override fun onResponse(call: Call<TodoResponse>, response: Response<TodoResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        todoBinding.loadingAnim.visibility = View.GONE
                        todoBinding.empty.visibility = View.GONE
                        val listData = response.body()!!.data
                        listTodo = listData
                        todoAdapter.setTodoItem(listTodo)
                        todoAdapter.notifyDataSetChanged()

                        with(todoBinding.rvTodoList) {
                            layoutManager = LinearLayoutManager(this@TodoActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = todoAdapter
                        }
                    } else if (response.body()!!.status == "empty") {
                        todoBinding.empty.visibility = View.VISIBLE
                        todoBinding.loadingAnim.visibility = View.GONE
                        listTodo.clear()
                        todoAdapter.setTodoItem(listTodo)
                        todoAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toasty.error(this@TodoActivity, response.message(), Toasty.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                Toasty.error(this@TodoActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
            }
        })
    }
}