package com.jaylangkung.indirisma.todo_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaylangkung.indirisma.MainActivity
import com.jaylangkung.indirisma.R
import com.jaylangkung.indirisma.databinding.ActivityToDoBinding
import com.jaylangkung.indirisma.databinding.BottomSheetTodoBinding
import com.jaylangkung.indirisma.retrofit.DataService
import com.jaylangkung.indirisma.retrofit.RetrofitClient
import com.jaylangkung.indirisma.retrofit.response.DefaultResponse
import com.jaylangkung.indirisma.retrofit.response.TodoResponse
import com.jaylangkung.indirisma.utils.Constants
import com.jaylangkung.indirisma.utils.ErrorHandler
import com.jaylangkung.indirisma.utils.MySharedPreferences
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToDoBinding
    private lateinit var addTodoBinding: BottomSheetTodoBinding
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var todoAdapter: TodoAdapter
    private var listTodo: ArrayList<TodoEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myPreferences = MySharedPreferences(this@TodoActivity)
        todoAdapter = TodoAdapter()

        val tokenAuth = getString(R.string.token_auth, myPreferences.getValue(Constants.TokenAuth).toString())
        val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()

        binding.apply {
            btnBack.setOnClickListener { onBackPressed() }

            fabAddTodo.setOnClickListener {
                addTodoBinding = BottomSheetTodoBinding.inflate(layoutInflater)

                val dialog = BottomSheetDialog(this@TodoActivity)
                val btnSave = addTodoBinding.btnSaveTodo

                btnSave.setOnClickListener {
                    loadingAnim.visibility = View.VISIBLE
                    val todo = addTodoBinding.inputTodo.text.toString()
                    val service = RetrofitClient().apiRequest().create(DataService::class.java)
                    service.insertTodo(idadmin, todo, tokenAuth, "true").enqueue(object : Callback<DefaultResponse> {
                        override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                            if (response.isSuccessful) {
                                if (response.body()!!.status == "success") {
                                    getTodo(tokenAuth)
                                    Toasty.success(this@TodoActivity, response.body()!!.message, Toasty.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                            loadingAnim.visibility = View.GONE
                            Toasty.error(this@TodoActivity, t.message.toString(), Toasty.LENGTH_LONG).show()
                        }
                    })
                    dialog.dismiss()
                }
                dialog.setCancelable(true)
                dialog.setContentView(addTodoBinding.root)
                dialog.show()
            }
        }

        getTodo(tokenAuth)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@TodoActivity, MainActivity::class.java))
        finish()
    }

    private fun getTodo(tokenAuth: String) {
        val service = RetrofitClient().apiRequest().create(DataService::class.java)
        service.getTodo(tokenAuth, "true").enqueue(object : Callback<TodoResponse> {
            override fun onResponse(call: Call<TodoResponse>, response: Response<TodoResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == "success") {
                        binding.loadingAnim.visibility = View.GONE
                        binding.empty.visibility = View.GONE
                        val listData = response.body()!!.data
                        listTodo = listData
                        todoAdapter.setItem(listTodo)
                        todoAdapter.notifyItemRangeChanged(0, listTodo.size)

                        with(binding.rvTodoList) {
                            layoutManager = LinearLayoutManager(this@TodoActivity)
                            itemAnimator = DefaultItemAnimator()
                            setHasFixedSize(true)
                            adapter = todoAdapter
                        }
                        todoAdapter.setOnItemClickCallback(object : TodoAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ArrayList<TodoEntity>, position: Int) {
                                val idadmin = myPreferences.getValue(Constants.USER_IDADMIN).toString()
                                val idtodoList = listTodo[position].idtodo_list

                                val mDialog = MaterialDialog.Builder(this@TodoActivity as Activity)
                                    .setTitle("Selesaikan ToDo")
                                    .setMessage("Apakah Anda yakin ingin menyelesaikan ToDo ini?")
                                    .setCancelable(true)
                                    .setPositiveButton(getString(R.string.yes), R.drawable.ic_checked)
                                    { dialogInterface, _ ->
                                        service.editTodo(idtodoList, idadmin, tokenAuth, "true")
                                            .enqueue(object : Callback<DefaultResponse> {
                                                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                                                    if (response.isSuccessful) {
                                                        if (response.body()!!.status == "success") {
                                                            Toasty.success(this@TodoActivity, "Todo berhasil diselesaikan", Toasty.LENGTH_LONG).show()
                                                            listTodo.removeAt(position)
                                                            if (listTodo.isEmpty()) {
                                                                binding.empty.visibility = View.VISIBLE
                                                                binding.loadingAnim.visibility = View.GONE
                                                                listTodo.clear()
                                                            }
                                                            todoAdapter.setItem(listTodo)
                                                            todoAdapter.notifyItemRangeChanged(0, listTodo.size)
                                                        }
                                                    } else {
                                                        binding.loadingAnim.visibility = View.GONE
                                                        ErrorHandler().responseHandler(
                                                            this@TodoActivity,
                                                            "editTodo | onResponse", response.message()
                                                        )
                                                    }
                                                }

                                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                                    binding.loadingAnim.visibility = View.GONE
                                                    ErrorHandler().responseHandler(
                                                        this@TodoActivity,
                                                        "editTodo | onResponse", t.message.toString()
                                                    )
                                                }
                                            })
                                        dialogInterface.dismiss()
                                    }
                                    .setNegativeButton(getString(R.string.no), R.drawable.ic_close)
                                    { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .build()
                                // Show Dialog
                                mDialog.show()
                            }

                        })
                    } else if (response.body()!!.status == "empty") {
                        binding.empty.visibility = View.VISIBLE
                        binding.loadingAnim.visibility = View.GONE
                        listTodo.clear()
                        todoAdapter.setItem(listTodo)
                        todoAdapter.notifyItemRangeChanged(0, listTodo.size)
                    }
                } else {
                    binding.loadingAnim.visibility = View.GONE
                    ErrorHandler().responseHandler(
                        this@TodoActivity,
                        "getTodo | onResponse", response.message()
                    )
                }
            }

            override fun onFailure(call: Call<TodoResponse>, t: Throwable) {
                binding.loadingAnim.visibility = View.GONE
                ErrorHandler().responseHandler(
                    this@TodoActivity,
                    "getTodo | onFailure", t.message.toString()
                )
            }
        })
    }

}