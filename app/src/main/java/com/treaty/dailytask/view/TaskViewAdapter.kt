package com.treaty.dailytask.view

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.treaty.dailytask.databinding.TaskViewBinding
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel

class TaskViewAdapter(
    private var taskGroupObject: List<TaskGroupModel>,
    private val taskGroupEvent: TaskGroupEvent
) : RecyclerView.Adapter<TaskViewAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = TaskViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.binding.addTaskBtn.setOnClickListener { taskGroupEvent.onClickAdd(position) }
        holder.binding.removeTaskBtn.setOnClickListener { taskGroupEvent.onClickRemove(position) }
        holder.binding.categoryTitleTxt.text = taskGroupObject[position].categoryID
        holder.binding.priceTxt.text = "$ ${taskGroupObject[position].totalPrice}"
        holder.binding.lastUpdateTxt.text = "Last added date: ${taskGroupObject[position].lastUpdate} - $ ${taskGroupObject[position].lastPrice}"
        holder.binding.taskViewLayout.backgroundTintList = ColorStateList.valueOf(taskGroupObject[position].backgroundColor)
    }

    override fun getItemCount(): Int {
        return taskGroupObject.size
    }

    inner class TaskViewHolder(val binding: TaskViewBinding) : ViewHolder(binding.root)

    interface TaskGroupEvent {
        fun onClickAdd(index: Int)
        fun onClickRemove(index: Int)
    }
}