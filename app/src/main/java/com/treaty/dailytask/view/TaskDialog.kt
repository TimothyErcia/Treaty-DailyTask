package com.treaty.dailytask.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.TaskDialogBinding
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskDialog(
    private val categoryID: String = "",
) : DialogFragment() {

    private lateinit var binding: TaskDialogBinding
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()
    private val toastMessageResult = MutableStateFlow("")

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
        binding = TaskDialogBinding.inflate(layoutInflater)
        if (categoryID.isNotEmpty()) {
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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val price = binding.priceInput.text.toString()
            val category = categoryID.ifEmpty { binding.categoryInput.selectedItem.toString() }
            val backgroundColor = getCategoryColor(category)
            val newTask = taskGroupViewModel.createNewTask(price)
            newTask.onSuccess { res ->
                val currentTaskList = arrayListOf(res)
                insertCurrentTask(category, currentTaskList, backgroundColor)
            }.onFailure { toastMessageResult.value = it.message.toString() }

            showToast(toastMessageResult.value)
        }
    }

    private suspend fun insertCurrentTask(category: String, currentTaskList: ArrayList<TaskModel>, backgroundColor: Int) {
        val taskGroupResult =
            taskGroupViewModel.createTaskGroup(category, currentTaskList, backgroundColor)
        taskGroupResult.onSuccess { res ->
            taskGroupViewModel.getCategoryAndInsert(res)
            toastMessageResult.value = taskGroupViewModel.insertResultMessage.value
            dismiss()
        }.onFailure { toastMessageResult.value = it.message.toString() }
    }

    private fun onCancel() {
        dialog?.dismiss()
    }

    private fun initializeCategories() {
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.categoryInput.adapter = arrayAdapter
    }

    private fun getCategoryColor(selectedCategory: String): Int {
        val categories = resources.getStringArray(R.array.categoryColors)
        val categoryColor =
            categories.filter { it.lowercase().contains(selectedCategory.lowercase()) }
        if (categoryColor.isNotEmpty()) {
            val colorResource =
                resources.getIdentifier(categoryColor[0], "color", requireContext().packageName)
            return resources.getColor(colorResource)
        }
        return resources.getColor(R.color.white)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.priceInput.setText("")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
