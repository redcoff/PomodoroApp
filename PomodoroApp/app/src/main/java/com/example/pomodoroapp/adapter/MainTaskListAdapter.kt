package com.example.pomodoroapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.MainTaskItemBinding
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.model.Task

sealed class DataItem {

}

class MainTaskListAdapter(val clickListener: MainTaskListener) :  ListAdapter<MainTask, MainTaskListAdapter.TaskViewHolder>(Companion) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MainTaskItemBinding.inflate(layoutInflater)

        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.binding.mainTask = currentTask
        holder.binding.executePendingBindings()
    }


    class TaskViewHolder(val binding: MainTaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<MainTask>() {
        override fun areItemsTheSame(oldItem: MainTask, newItem: MainTask): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: MainTask, newItem: MainTask): Boolean = oldItem.name == newItem.name
    }

    class MainTaskListener(val clickListener: (taskName: String) -> Unit) {
        fun onClick(task: MainTask) = clickListener(task.name)
    }

}