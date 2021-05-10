package com.example.pomodoroapp.model

data class MinorTask(
    val type: Type = Type.DEFERRABLE,
    val name: String = "",
    val uid: String = "",
) {
    enum class Type {
        DEFERRABLE, // could be postponed// odkladne
        INDELIBLE   // cant be postponed// neodkladne
    }

}