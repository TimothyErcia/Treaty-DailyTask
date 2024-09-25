package com.treaty.dailytask.view

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.PackageManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.TaskDialogBinding
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.model.TaskGroup.TaskGroupModel
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

class TaskDialog(
    private val categoryID: String = "",
) : DialogFragment() {

    private lateinit var binding: TaskDialogBinding
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()
    private var currentTaskList = mutableListOf<TaskModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initializeUI()
        initializeCategories()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TaskDialogBinding.inflate(LayoutInflater.from(requireContext()))
        if(categoryID.isNotEmpty()) {
            binding.categoryInput.visibility = GONE
        }
        val builder = MaterialAlertDialogBuilder(requireContext(), R.drawable.taskgroup_corners)
        builder.setView(binding.root)

        return builder.create()
    }

    private fun initializeUI() {
        binding.cancelBtn.setOnClickListener { onCancel() }
        binding.proceedBtn.setOnClickListener { onAdd() }
    }

    private fun onAdd() {
        val price = binding.priceInput.text.toString()
        val category = if(categoryID.isEmpty()) binding.categoryInput.selectedItem.toString() else categoryID
        val backgroundColor = getCategoryColor(category)
        val newTask = taskGroupViewModel.createNewTask(price.toInt())
        currentTaskList.add(newTask)

        viewLifecycleOwner.lifecycleScope.launch {
            taskGroupViewModel.getAllTaskByCategory(category).collectLatest {
                if(it.isNotEmpty()) {
                    currentTaskList.addAll(it.first().taskModelList)
                }
                val taskGroupModel = taskGroupViewModel.createTaskGroup(category, currentTaskList, backgroundColor)
                taskGroupViewModel.insertOrUpdateTaskGroup(taskGroupModel)
            }
        }
        dismiss()
    }

    private fun onCancel() {
        dialog?.dismiss()
    }

    private fun initializeCategories() {
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.categoryInput.adapter = arrayAdapter
    }

    private fun getCategoryColor(selectedCategory: String): Int {
        val categories = resources.getStringArray(R.array.categoryColors)
        val categoryColor = categories.filter {it.lowercase().contains(selectedCategory.lowercase()) }
        if(categoryColor.isNotEmpty()) {
            val colorResource = resources.getIdentifier(categoryColor[0], "color", requireContext().packageName)
            return resources.getColor(colorResource)
        }
        return resources.getColor(R.color.white)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.priceInput.setText("")
    }
}