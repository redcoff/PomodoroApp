package com.example.pomodoroapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.R
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.model.Task


class MainTaskListAdapter(private val interaction: Interaction? = null) : ListAdapter<MainTask, MainTaskListAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.task_text)

        fun bind(text: String?) {
            wordItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_task_item, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    interface Interaction {
        fun itemClicked(item: Task)
    }

    class TasksComparator : DiffUtil.ItemCallback<MainTask>() {
        override fun areItemsTheSame(oldItem: MainTask, newItem: MainTask): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MainTask, newItem: MainTask): Boolean {
            return oldItem.name == newItem.name
        }
    }
}