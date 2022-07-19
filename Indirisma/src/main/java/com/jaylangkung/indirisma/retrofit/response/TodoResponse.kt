package com.jaylangkung.indirisma.retrofit.response

import com.jaylangkung.indirisma.todo_list.TodoEntity

class TodoResponse(
    var status: String = "",
    var data: ArrayList<TodoEntity>
)

