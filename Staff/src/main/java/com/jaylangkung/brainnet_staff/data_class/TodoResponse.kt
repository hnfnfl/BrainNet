package com.jaylangkung.brainnet_staff.data_class

class TodoResponse(
    var status: String = "",
    var data: ArrayList<TodoEntity>
)

class TodoEntity(
    var idtodo_list: String = "",
    var todo: String = "",
    var nama: String = ""
)