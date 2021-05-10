package com.example.pomodoroapp.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.R
import com.example.pomodoroapp.adapter.MainTaskListAdapter.TextViewHolder.Companion.from
import com.example.pomodoroapp.databinding.MainTaskItemBinding
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.model.Task
import kotlinx.android.synthetic.main.main_task_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1

sealed class DataItem {
    abstract val name: String

    data class MainTaskItem(val mainTask: MainTask): DataItem(){
        override val name = mainTask.name
    }
    object Header: DataItem(){
        override val name = ""
    }

}

class MainTaskListAdapter(val clickListener: MainTaskListener) :  ListAdapter<DataItem, RecyclerView.ViewHolder>(MainTaskDiffCallback()) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> TaskViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> {
                val taskitem = getItem(position) as DataItem.MainTaskItem
                holder.bind(taskitem.mainTask, clickListener)
            }
        }
    }

    fun addHeaderAndSubmitList(list: List<MainTask>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.MainTaskItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.main_task_header, parent, false)
                view.text.text = LocalDate.now().toString()
                return TextViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.MainTaskItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


    class TaskViewHolder(val binding: MainTaskItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(task: MainTask, clickListener: MainTaskListener) {
            binding.mainTask = task
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MainTaskItemBinding.inflate(layoutInflater, parent, false)
                return TaskViewHolder(binding)
            }
        }
    }

    /*companion object: DiffUtil.ItemCallback<MainTask>() {
        override fun areItemsTheSame(oldItem: MainTask, newItem: MainTask): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: MainTask, newItem: MainTask): Boolean = oldItem.name == newItem.name
    }*/

    class MainTaskListener(val clickListener: (taskName: String) -> Unit) {
        fun onClick(task: MainTask) = clickListener(task.name)
        fun onLongClick(task: MainTask) = clickListener(task.name)
    }

    class MainTaskDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.name == newItem.name
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

}