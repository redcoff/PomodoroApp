package com.example.pomodoroapp.model

import java.util.*

data class MainTask(val pomodoros:Int, val description:String, val date:Date, override val name: String): Task() {

}