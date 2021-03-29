package com.example.pomodoroapp.model



data class MinorTask(val type:Type, override val name: String) : Task(){

    enum class Type {
        DEFERRABLE, // could be postponed
        INDELIBLE   // cant be postponed
    }
}