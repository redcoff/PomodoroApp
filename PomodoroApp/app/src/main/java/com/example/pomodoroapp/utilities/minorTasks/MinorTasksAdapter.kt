package com.example.pomodoroapp.utilities.minorTasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.R
import com.example.pomodoroapp.model.MinorTask
import kotlinx.android.synthetic.main.minor_task_item.view.*

class MinorTasksAdapter : ListAdapter<MinorTask, MinorTasksAdapter.ItemViewholder>(DiffCallback())  {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
            return ItemViewholder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.minor_task_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: MinorTasksAdapter.ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

        class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: MinorTask) = with(itemView) {
                task_text.text = item.name
                setOnClickListener {
                    // TODO: Handle on click
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MinorTask>() {
        override fun areItemsTheSame(oldItem: MinorTask, newItem: MinorTask): Boolean {
        return oldItem.id == newItem.id
    }

        override fun areContentsTheSame(oldItem: MinorTask, newItem: MinorTask): Boolean {
        return oldItem == newItem
    }
    }