package com.example.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    var taskItems = MutableLiveData<MutableList<TaskItem>>()
    private val sharedPreferencesHelper = SharedPreferencesHelper(application)

    init {
        taskItems.value = sharedPreferencesHelper.loadTaskList()
        sortTaskItems()  // Sort the task list on initialization
    }

    // Sort task items based on completion status and due date
    private fun sortTaskItems() {
        val sortedList = taskItems.value?.sortedWith(compareBy<TaskItem> { it.isCompleted }
            .thenBy { it.dueDate })?.toMutableList()
        taskItems.value = sortedList
    }

    fun addTaskItem(newTask: TaskItem) {
        val list = taskItems.value ?: mutableListOf()
        list.add(newTask)
        taskItems.value = list
        sortTaskItems()  // Sort the list after adding a new task
        sharedPreferencesHelper.saveTaskList(list)
    }

    fun updateTaskItem(id: UUID, name: String, dueTime: LocalTime?, dueDate: LocalDate?, category: String?) {
        val list = taskItems.value ?: return
        val task = list.find { it.id == id } ?: return
        task.name = name
        task.dueTime = dueTime
        task.dueDate = dueDate
        task.category = category ?: "Default Category"
        taskItems.value = list
        sortTaskItems()  // Sort the list after updating a task
        sharedPreferencesHelper.saveTaskList(list)
    }

    fun setCompleted(taskItem: TaskItem) {
        val list = taskItems.value ?: return
        val task = list.find { it.id == taskItem.id } ?: return
        if (task.completedDate == null) {
            task.completedDate = LocalDate.now()
        }
        taskItems.value = list
        sortTaskItems()  // Sort the list after marking a task as completed
        sharedPreferencesHelper.saveTaskList(list)
    }

    fun deleteTaskItem(taskItem: TaskItem) {
        val list = taskItems.value ?: return
        list.remove(taskItem)
        taskItems.value = list
        sortTaskItems()  // Sort the list after deleting a task
        sharedPreferencesHelper.saveTaskList(list)
    }
}
