package com.treaty.dailytask.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.TaskDialogBinding
import com.treaty.dailytask.model.Task.TaskModel
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskDialog(
    private val categoryID: String = "",
    private val taskGroupViewModel: TaskGroupViewModel
) : DialogFragment() {

    private lateinit var binding: TaskDialogBinding
    private var accumulatedPrice: Int = 0
    private var price: String = ""
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
        binding.addAccumulationBtn.setOnClickListener { onAddAccumulation() }
        binding.resetAccumulationBtn.setOnClickListener { onResetAccumulation() }
        binding.priceInput.doAfterTextChanged { it -> price = it.toString()}
    }

    private fun onAdd() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val category = categoryID.ifEmpty { binding.categoryInput.selectedItem.toString() }
            val backgroundColor = getCategoryColor(category)

            val finalPrice = if(accumulatedPrice > 0) accumulatedPrice.toString() else { price }
            val newTask = taskGroupViewModel.createNewTask(finalPrice)
            newTask.onSuccess { task ->
                insertCurrentTask(category, task, backgroundColor)
            }
        }
    }

    private suspend fun insertCurrentTask(category: String, newTask: TaskModel, backgroundColor: Int) {
        taskGroupViewModel.getCategoryAndInsert(category, newTask, backgroundColor)
        dismiss()
    }

    private fun onCancel() {
        taskGroupViewModel.setMessage(Result.success(""))
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
            return resources.getColor(colorResource, null)
        }
        return resources.getColor(R.color.white, null)
    }

    private fun onAddAccumulation() {
        accumulatedPrice += price.toInt()
        binding.priceAccumulationTxt.text = "Total: $${accumulatedPrice}"
        binding.priceInput.setText("")
    }

    private fun onResetAccumulation() {
        accumulatedPrice = 0
        price = ""
        binding.priceInput.setText("")
        binding.priceAccumulationTxt.text = "Total: "
    }
}
