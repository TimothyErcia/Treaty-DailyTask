package com.treaty.dailytask.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.FragmentHomeBinding
import com.treaty.dailytask.model.Task
import com.treaty.dailytask.model.TaskGroup.TaskGroupObject
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TaskViewAdapter
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                initializeTaskGroup()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                initializeTotalSum()
            }
        }
    }

    private suspend fun initializeTaskGroup() {
        taskGroupViewModel.taskGroup
            .collectLatest { data ->
                adapter = TaskViewAdapter(data, object:TaskViewAdapter.TaskGroupEvent {
                    override fun onClickAdd(index: Int) {
                        Log.d("TASKGROUP", "onClickAdd: ")
                        val taskDialog = TaskDialog(false)
                        taskDialog.show(childFragmentManager, "sometag")
                    }

                    override fun onClickRemove(index: Int) {
                        Log.d("TASKGROUP", "onClickRemove: ")
                    }
                })
                binding.taskGroupListView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.taskGroupListView.adapter = adapter
            }
    }

    private fun initializeAddTaskGroup() {
        binding.bottomLayout.addTaskGroupBtn.setOnClickListener {
            val taskDialog = TaskDialog(true)
            taskDialog.show(childFragmentManager, "sometag")
        }
    }

    private suspend fun initializeTotalSum() {
        taskGroupViewModel.totalSum.collectLatest {
            withContext(Dispatchers.Main) {
                binding.bottomLayout.totalSpendingTxt.text = "$ $it.00"
            }
        }
    }
}