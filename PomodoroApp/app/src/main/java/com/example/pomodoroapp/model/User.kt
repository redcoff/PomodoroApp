package com.example.pomodoroapp.model

data class User(val id:Int, val email:String, val password:String){
    private val notifications:MutableList<Notification> = ArrayList<Notification>()
    private val tasks:MutableList<Task> = ArrayList<Task>()

    fun addTask(task:Task){tasks.add(task)}
    fun addNotification(not:Notification) {notifications.add(not)}
}
