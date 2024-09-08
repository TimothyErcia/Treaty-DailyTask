package com.treaty.dailytask.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.TaskDialogBinding
import com.treaty.dailytask.model.Task
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskDialog(
    val isTaskGroup: Boolean
) : DialogFragment() {

    private lateinit var binding: TaskDialogBinding
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TaskDialogBinding.inflate(LayoutInflater.from(requireContext()))
        if(!isTaskGroup) {
            binding.categoryInputLayout.visibility = GONE
        }
        val builder = MaterialAlertDialogBuilder(requireContext(), R.drawable.taskgroup_corners)
        builder.setView(binding.root)

        initializeUI()

        return builder.create()
    }

    private fun initializeUI() {
        binding.cancelBtn.setOnClickListener { onCancel() }
        binding.proceedBtn.setOnClickListener { onAdd() }
    }

    private fun onAdd() {
        val price = binding.priceInput.text.toString()
        val category = binding.categoryInput.text.toString()

        val task = Task(price.toInt())
        val taskGroupObject = TaskGroupObject(category, realmListOf(task))

        viewLifecycleOwner.lifecycleScope.launch {
            taskGroupViewModel.createNewTaskGroup(taskGroupObject)
        }
        clearEntries()
    }

    private fun onCancel() {
        dialog?.dismiss()
    }

    private fun clearEntries() {
        binding.priceInput.setText("")
        binding.categoryInput.setText("")
    }
}