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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.treaty.dailytask.R
import com.treaty.dailytask.databinding.FragmentHomeBinding
import com.treaty.dailytask.model.Status
import com.treaty.dailytask.viewmodel.MenuViewModel
import com.treaty.dailytask.viewmodel.TaskGroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: TaskViewAdapter
    private val taskGroupViewModel: TaskGroupViewModel by viewModel()
    private val menuViewModel: MenuViewModel by viewModel()
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
        initializeDrawer()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { initializeTaskGroup() }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) { initializeNotificationToggle() }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) { initializeNotificationTime() }
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
                                    data[index].categoryID, null, taskGroupViewModel
                                )
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

                        override fun onClickTaskGroup(index: Int) {
                            taskDialog =
                                TaskDialog(data[index].categoryID, data[index], taskGroupViewModel)
                            showDialog()
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
            taskDialog = TaskDialog(
                currentTaskGroup = null,
                taskGroupViewModel = taskGroupViewModel
            )
            showDialog()
        }
    }

    private fun showDialog() {
        taskDialog.show(childFragmentManager, "sometag")
        childFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        showToast(taskGroupViewModel.resultMessage.value)
                    }
                    childFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                }
            },
            false)
    }

    private suspend fun showToast(message: Status) {
        if (message.statusMessage.isNotEmpty()) {
            binding.customSnackbar.root.visibility = View.VISIBLE
            binding.customSnackbar.snackbarLayout.backgroundTintList =
                ColorStateList.valueOf(getColor(message.statusValue))
            binding.customSnackbar.snackbarText.text = message.statusMessage
            binding.customSnackbar.root.translationY = 500f
            binding.customSnackbar.root
                .animate()
                .translationY(500f)
                .setDuration(500)
                .translationY(0f)
            delay(2000)
            binding.customSnackbar.root
                .animate()
                .translationY(0f)
                .setDuration(500)
                .translationY(500f)
            delay(1000)
            binding.customSnackbar.root.visibility = View.GONE
        }
    }

    private fun initializeDeleteAll() {
        binding.headerLayout.deleteAllBtn.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) { taskGroupViewModel.deleteAll() }
                showToast(taskGroupViewModel.resultMessage.value)
            }
        }
    }

    private fun getColor(statusValue: Boolean): Int {
        if (statusValue) {
            return resources.getColor(R.color.foodCategory)
        }
        return resources.getColor(R.color.personalCategory)
    }

    private fun initializeDrawer() {
        binding.headerLayout.menuBtn.setOnClickListener { binding.drawerLayout.open() }
        binding.sideMenuLayout.toggleBtn.setOnClickListener { triggerToggle() }
        binding.sideMenuLayout.timeTxt.setOnClickListener { initializeTime() }
        binding.sideMenuLayout.cloudSaveBtn.setOnClickListener {
            TODO("Button for manual sync save")
        }
    }

    private fun initializeTime() {
        val currentTime = LocalDateTime.now()
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select Notification Time")
                .setHour(currentTime.hour)
                .setMinute(currentTime.minute)
                .build()
        picker.show(childFragmentManager, "pickerTag")
        picker.addOnPositiveButtonClickListener {
            val selectedTime = LocalTime.of(picker.hour, picker.minute)
            binding.sideMenuLayout.timeTxt.text =
                selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
            menuViewModel.updateReminderTime(LocalDateTime.of(LocalDate.now(), selectedTime))
        }
    }

    private fun triggerToggle() {
        menuViewModel.notificationToggle()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            initializeNotificationToggle()
        }
    }

    private suspend fun initializeNotificationToggle() {
        menuViewModel.menuNotificationToggle.collectLatest {
            val resourceStr =
                if (it) {
                    R.drawable.toggle_left
                } else {
                    R.drawable.toggle_on
                }
            binding.sideMenuLayout.toggleBtn.setImageResource(resourceStr)
        }
    }

    private suspend fun initializeNotificationTime() {
        menuViewModel.menuTime.collectLatest {
            binding.sideMenuLayout.timeTxt.text = it.format(DateTimeFormatter.ofPattern("hh:mm a"))
        }
    }
}
