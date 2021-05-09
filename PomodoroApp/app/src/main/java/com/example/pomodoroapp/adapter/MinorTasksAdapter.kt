package com.example.pomodoroapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityMinorTasksBinding
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.model.Task


class MinorTasksAdapter(private val interaction: Interaction? = null) : ListAdapter<MinorTask, MinorTasksAdapter.TaskViewHolder>(TasksComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.from(parent,interaction)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }



    fun swapData(it: List<MinorTask>) {
        submitList(it.toMutableList())
    }

    class TaskViewHolder(private val binding: ActivityMinorTasksBinding,
                         private val interaction: Interaction?
                          ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        lateinit var storedItem: MinorTask

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: MinorTask) {
            storedItem = item
//            binding. = item.name
        }

        companion object {
            fun from(parent: ViewGroup, interaction: Interaction?): TaskViewHolder {
                val binding: ActivityMinorTasksBinding = ActivityMinorTasksBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )
                return TaskViewHolder(binding,interaction)
            }
        }

        override fun onClick(v: View?) {
            interaction?.itemClicked(storedItem)
        }
    }

    interface Interaction {
        fun itemClicked(item: MinorTask)
    }

    class TasksComparator : DiffUtil.ItemCallback<MinorTask>() {
        override fun areItemsTheSame(oldItem: MinorTask, newItem: MinorTask): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MinorTask, newItem: MinorTask): Boolean {
            return oldItem.name == newItem.name
        }
    }
}