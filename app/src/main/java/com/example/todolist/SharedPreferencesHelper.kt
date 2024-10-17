package com.example.todolist

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        const val TASKS_KEY = "task_list"
    }

    fun saveTaskList(taskList: List<TaskItem>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(taskList)
        editor.putString(TASKS_KEY, json)
        editor.apply()  // Asynchronously save changes
    }

    fun loadTaskList(): MutableList<TaskItem> {
        val json = sharedPreferences.getString(TASKS_KEY, null)
        val type = object : TypeToken<MutableList<TaskItem>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
