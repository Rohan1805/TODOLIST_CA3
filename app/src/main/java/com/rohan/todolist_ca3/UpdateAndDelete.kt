package com.rohan.todolist_ca3

interface UpdateAndDelete{
    fun modifyItems(itemUID:String,isDone:Boolean)
    fun onItemDelete(itemUID: String)
}