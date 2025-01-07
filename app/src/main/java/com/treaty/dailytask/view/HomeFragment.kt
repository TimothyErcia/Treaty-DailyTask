package com.treaty.dailytask.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.FragmentHomeBinding
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TaskViewAdapter
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()
    private lateinit var taskDialog: TaskDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAddTaskGroup()
        initializeDeleteAll()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                initializeTaskGroup()
            }
        }
    }

    private suspend fun initializeTaskGroup() {
        taskGroupViewModel.taskGroup
            .collectLatest { data ->
                adapter = TaskViewAdapter(data, object:TaskViewAdapter.TaskGroupEvent {
                    override fun onClickAdd(index: Int) {
                        taskDialog = TaskDialog(data[index].categoryID)
                        showDialog()
                    }

                    override fun onClickRemove(index: Int) {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            withContext(Dispatchers.IO) {
                                taskGroupViewModel.deleteByCategory(data[index].categoryID)
                            }
                            showToast(taskGroupViewModel.resultMessage.value)
                        }
                        binding.taskGroupListView.removeViewAt(index)
                    }
                })
                binding.taskGroupListView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.taskGroupListView.adapter = adapter

                binding.bottomLayout.totalSpendingTxt.text = "$ ${taskGroupViewModel.getTotalSum(data)}.00"

                binding.headerLayout.deleteAllBtn.visibility = if(taskGroupViewModel.taskGroup.value.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
    }

    private fun initializeAddTaskGroup() {
        binding.bottomLayout.addTaskGroupBtn.setOnClickListener {
            taskDialog = TaskDialog()
            showDialog()
        }
    }

    private fun showDialog() {
        taskDialog.show(childFragmentManager, "sometag")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun initializeDeleteAll() {
        binding.headerLayout.deleteAllBtn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    taskGroupViewModel.deleteAll()
                }
                showToast(taskGroupViewModel.resultMessage.value)
            }
        }
    }
}