package com.example.pomodoroapp.model

import com.google.firebase.Timestamp
import java.util.*

data class MainTask(
    val name:String = "",
    val uid: String = "",
    val pomodoros:Int = 0,
    val description:String = "",
    val date:Timestamp? = null,
    val lat: Double = 0.0,
    val long: Double = 0.0
){
    fun pomodoroFormat(): String {
        var format = ""
        for(i in 1..pomodoros){
            format += "|"
        }
        return format
    }
}