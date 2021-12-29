package com.jaylangkung.brainnet_staff.todo_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.brainnet_staff.MainActivity
import com.jaylangkung.brainnet_staff.R
import com.jaylangkung.brainnet_staff.databinding.ActivityToDoBinding
import com.jaylangkung.brainnet_staff.databinding.BottomSheetTodoBinding
import com.jaylangkung.brainnet_staff.retrofit.DataService
import com.jaylangkung.brainnet_staff.retrofit.RetrofitClient
import com.jaylangkung.brainnet_staff.retrofit.response.DefaultResponse
import com.jaylangkung.brainnet_staff.retrofit.response.TodoResponse
import com.jaylangkung.brainnet_staff.utils.Constants
import com.jaylangkung.brainnet_staff.utils.MySharedPreferences
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoActivity : AppCompatActivity() {

    private lateinit var todoBinding: ActivityToDoBinding
    private lateinit var addTodoBinding: BottomSheetTodoBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var todoAdapter: TodoAdapter
    private var listTodo: ArrayList<TodoEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        todoBinding = ActivityToDoBinding.inflate(layoutInflater)
        setContentView(todoBinding.root)
        myPreferences = MySharedPreferences(this@TodoActivity)
        todoAdapter = TodoAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()

        todoBinding.btnBack.setOnClickListener {
            onBackPressed()
        }

        todoBinding.fabAddTodo.setOnClickListener {
            addTodoBinding = BottomSheetTodoBinding.inflate(layoutInflater)

            val dialog = BottomSheetDialog(this@TodoActivity)
            val btnSave = addTodoBinding.btnSaveTodo

            btnSave.setOnClickListener {
                todoBinding.loadingAnim.visibility = View.VISIBLE
                val todo = addTodoBinding.inputTodo.text.toString()
                val service = RetrofitClient().apiRequest().create(DataService::class.java)
                service.insertTodo(idadmin, todo, tokenAuth).enqueue(object : Callback<DefaultResponse> {
                    override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                        if (response.isSuccessful) {
                            if (response.body()!!.status == "success") {
                                getTodo(tokenAuth)
                                Toasty.success(this@TodoActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        todoBinding.loadingAnim.visibility = View.GONE
                        Toasty.error(this@TodoActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
                    }
                })
                dialog.dismiss()
            }
            dialog.setCancelable(true)
            dialog.setContentView(addTodoBinding.root)
            dialog.show()
        }
        getTodo(tokenAuth)
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