package com.example.pomodoroapp.model

data class MinorTask(val type:Type, override var name: String, override var id: Int) : Task(){
    enum class Type {
        DEFERRABLE, // could be postponed
        INDELIBLE   // cant be postponed
    }
}