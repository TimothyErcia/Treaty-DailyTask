package com.treaty.dailytask.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.FragmentHomeBinding
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
            repeatOnLifecycle(Lifecycle.State.STARTED) { initializeTaskGroup() }
        }
    }

    private suspend fun initializeTaskGroup() {

        taskGroupViewModel.taskGroup.collectLatest { data ->
            adapter =
                TaskViewAdapter(
                    data,
                    object : TaskViewAdapter.TaskGroupEvent {
                        override fun onClickAdd(index: Int) {
                            taskDialog =
                                TaskDialog(
                                    data[index].categoryID, taskGroupViewModel = taskGroupViewModel)
                            showDialog()
                        }

                        override fun onClickRemove(index: Int) {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                withContext(Dispatchers.IO) {
                                    taskGroupViewModel.deleteByCategory(data[index].categoryID)
                                }
                                showToast()
                            }
                            binding.taskGroupListView.removeViewAt(index)
                        }
                    })
            binding.taskGroupListView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.taskGroupListView.adapter = adapter

            binding.bottomLayout.totalSpendingTxt.text =
                "$ ${taskGroupViewModel.getTotalSum(data)}.00"

            binding.headerLayout.deleteAllBtn.visibility =
                if (taskGroupViewModel.taskGroup.value.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun initializeAddTaskGroup() {
        binding.bottomLayout.addTaskGroupBtn.setOnClickListener {
            taskDialog = TaskDialog(taskGroupViewModel = taskGroupViewModel)
            showDialog()
        }
    }

    private fun showDialog() {
        taskDialog.show(childFragmentManager, "sometag")
        childFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) { showToast() }
                    childFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                }
            },
            false)
    }

    private suspend fun showToast() {
        val result = taskGroupViewModel.resultMessage.value
        if (result.statusMessage.isNotEmpty()) {
            binding.customSnackbar.root.visibility = View.VISIBLE
            binding.customSnackbar.snackbarLayout.backgroundTintList =
                ColorStateList.valueOf(getColor(result.statusValue))
            binding.customSnackbar.snackbarText.text = result.statusMessage
            binding.customSnackbar.root.translationY = 500f
            binding.customSnackbar.root
                .animate()
                .translationY(500f)
                .setDuration(950)
                .translationY(0f)
            delay(2000)
            binding.customSnackbar.root
                .animate()
                .translationY(0f)
                .setDuration(800)
                .translationY(500f)
            delay(1000)
            binding.customSnackbar.root.visibility = View.GONE
        }
    }

    private fun initializeDeleteAll() {
        binding.headerLayout.deleteAllBtn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) { taskGroupViewModel.deleteAll() }
                showToast()
            }
        }
    }

    private fun getColor(statusValue: Boolean): Int {
        if (statusValue) {
            return resources.getColor(R.color.foodCategory)
        }
        return resources.getColor(R.color.personalCategory)
    }
}
