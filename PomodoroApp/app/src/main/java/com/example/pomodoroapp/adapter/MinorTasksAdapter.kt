package com.example.pomodoroapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.databinding.MinorTaskItemBinding
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.ui.AddMinorTaskActivity
import com.example.pomodoroapp.ui.EditMinorTaskActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_ITEM = 1

sealed class MinorDataItem {
    abstract val name: String
    data class MinorTaskItem(val minorTask: MinorTask): MinorDataItem(){
        override val name = minorTask.name
        val type = minorTask.type
    }
}


class MinorTasksAdapter(val clickListener: MinorTaskListener) : ListAdapter<MinorDataItem, RecyclerView.ViewHolder>(
    MinorTaskDiffCallback()
) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> TaskViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> {
                val taskitem = getItem(position) as MinorDataItem.MinorTaskItem
                holder.bind(taskitem.minorTask, clickListener)
            }
        }
    }

    fun addSubmitList(list: List<MinorTask>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf()
                else -> list.map { MinorDataItem.MinorTaskItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MinorDataItem.MinorTaskItem -> ITEM_VIEW_TYPE_ITEM
        }
    }


    class TaskViewHolder(private val binding: MinorTaskItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: MinorTask, clickListener: MinorTaskListener) {
             binding.minorTask = item
             binding.executePendingBindings()
        }

        init {
            binding.cardItems.setOnClickListener {
                val int = Intent(it.context,EditMinorTaskActivity::class.java)
                int.putExtra("taskName",binding.taskText.text.toString())
                int.putExtra("type",binding.minorTask?.type.toString())
                it.context.startActivity(int)
            }
        }


        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MinorTaskItemBinding.inflate(layoutInflater, parent, false)
                return TaskViewHolder(binding)
            }
        }
    }


    class MinorTaskListener(val clickListener: (taskName: String) -> Unit) {
        fun onClick(task: MinorTask) = clickListener(task.name)
        fun onLongClick(task: MinorTask) = clickListener(task.name)
    }

    class MinorTaskDiffCallback : DiffUtil.ItemCallback<MinorDataItem>() {
        override fun areItemsTheSame(oldItem: MinorDataItem, newItem: MinorDataItem): Boolean {
            return oldItem.name == newItem.name
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: MinorDataItem, newItem: MinorDataItem): Boolean {
            return oldItem == newItem
        }
    }


}