package com.example.pomodoroapp.model

data class MinorTask(
    val type: Type = Type.DEFERRABLE,
    val name: String = "",
    val id: Int = -1,
    val uid: Int = -1,
) {
    enum class Type {
        DEFERRABLE, // could be postponed
        INDELIBLE   // cant be postponed
    }
}