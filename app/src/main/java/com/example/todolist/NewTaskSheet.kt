package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime

class NewTaskSheet(private val taskItem: TaskItem?) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTime: LocalTime? = null
    private var dueDate: LocalDate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = ViewModelProvider(requireActivity()).get(TaskViewModel::class.java)

        // Populate fields if editing
        taskItem?.let {
            binding.taskTitle.text = "Edit Task"
            binding.name.text = Editable.Factory.getInstance().newEditable(it.name)
            dueTime = it.dueTime // References LocalTime
            dueDate = it.dueDate // References LocalDate
            if (dueTime != null) {
                updateTimeButtonText()
            }
            if (dueDate != null) {
                updateDateButtonText()
            }
            binding.taskCategorySpinner.setSelection(getCategoryIndex(it.category))
        } ?: run {
            binding.taskTitle.text = "New Task"
        }

        binding.saveButton.setOnClickListener {
            saveAction()
        }

        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }

        binding.datePickerButton.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openTimePicker() {
        if (dueTime == null) {
            dueTime = LocalTime.now()
        }
        val listener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(requireActivity(), listener, dueTime!!.hour, dueTime!!.minute, true)
        dialog.setTitle("Task Due Time")
        dialog.show()
    }

    private fun openDatePicker() {
        if (dueDate == null) {
            dueDate = LocalDate.now()
        }
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dueDate = LocalDate.of(year, month + 1, dayOfMonth) // Month is 0-based
            updateDateButtonText()
        }
        val dialog = DatePickerDialog(requireActivity(), listener, dueDate!!.year, dueDate!!.monthValue - 1, dueDate!!.dayOfMonth)
        dialog.setTitle("Select Due Date")
        dialog.show()
    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d", dueTime!!.hour, dueTime!!.minute)
    }

    private fun updateDateButtonText() {
        binding.datePickerButton.text = String.format("%02d/%02d/%04d", dueDate!!.dayOfMonth, dueDate!!.monthValue, dueDate!!.year)
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val category = binding.taskCategorySpinner.selectedItem?.toString()

        if (taskItem == null) {
            // Create new task
            val newTask = TaskItem(name, dueTime, dueDate, null, category ?: "Default Category")
            taskViewModel.addTaskItem(newTask)
        } else {
            // Update existing task
            taskViewModel.updateTaskItem(taskItem.id, name, dueTime, dueDate, category)
        }

        binding.name.setText("")
        dismiss()
    }

    private fun getCategoryIndex(category: String?): Int {
        val categories = resources.getStringArray(R.array.task_categories)
        return if (category != null) {
            categories.indexOf(category)
        } else {
            0 // Default to the first category if null
        }
    }
}
