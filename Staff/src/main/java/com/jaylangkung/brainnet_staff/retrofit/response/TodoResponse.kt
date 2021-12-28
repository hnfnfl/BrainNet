package com.jaylangkung.brainnet_staff.retrofit.response

import com.jaylangkung.brainnet_staff.todo_list.TodoEntity

class TodoResponse(
    var status: String = "",
    var data: ArrayList<TodoEntity>
)

