package com.example.todolist

import TaskItemAdapter
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), TaskItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels()

    private var currentCategory: String = "All"
    private val NOTIFICATION_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        // Check notification permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
        }

        setupCategoryButtons()

        binding.newTaskButton.setOnClickListener {
            NewTaskSheet(null).show(supportFragmentManager, "newTaskTag")
            sendNewTaskNotification()
        }

        setRecyclerView()

        binding.stopwatchButton.setOnClickListener {
            val intent = Intent(this, Stopwatch::class.java)
            startActivity(intent)
        }
    }

    private fun setupCategoryButtons() {
        binding.allTab.isSelected = true
        binding.allTab.setTextColor(Color.BLUE)

        binding.allTab.setOnClickListener {
            updateCategorySelection("All")
        }
        binding.workTab.setOnClickListener {
            updateCategorySelection("Work")
        }
        binding.personalTab.setOnClickListener {
            updateCategorySelection("Personal")
        }
        binding.wishlistTab.setOnClickListener {
            updateCategorySelection("Wishlist")
        }
    }

    private fun updateCategorySelection(category: String) {
        resetTabSelection()

        when (category) {
            "All" -> {
                binding.allTab.isSelected = true
                binding.allTab.setTextColor(Color.BLUE)
            }
            "Work" -> {
                binding.workTab.isSelected = true
                binding.workTab.setTextColor(Color.BLUE)
            }
            "Personal" -> {
                binding.personalTab.isSelected = true
                binding.personalTab.setTextColor(Color.BLUE)
            }
            "Wishlist" -> {
                binding.wishlistTab.isSelected = true
                binding.wishlistTab.setTextColor(Color.BLUE)
            }
        }

        currentCategory = category
        setRecyclerView()
        checkAllTasksCompleted()
    }

    private fun resetTabSelection() {
        binding.allTab.isSelected = false
        binding.workTab.isSelected = false
        binding.personalTab.isSelected = false
        binding.wishlistTab.isSelected = false

        binding.allTab.setTextColor(Color.GRAY)
        binding.workTab.setTextColor(Color.GRAY)
        binding.personalTab.setTextColor(Color.GRAY)
        binding.wishlistTab.setTextColor(Color.GRAY)
    }

    private fun setRecyclerView() {
        taskViewModel.taskItems.observe(this) { taskList ->
            val filteredList = when (currentCategory) {
                "Work" -> taskList.filter { it.category == "Work" }
                "Personal" -> taskList.filter { it.category == "Personal" }
                "Wishlist" -> taskList.filter { it.category == "Wishlist" }
                else -> taskList
            }

            binding.todoListRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = TaskItemAdapter(filteredList, this@MainActivity)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Channel for task notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TASK_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNewTaskNotification() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationBuilder = NotificationCompat.Builder(this, "TASK_CHANNEL")
                .setSmallIcon(R.drawable.notifications_24)
                .setContentTitle("New Task Added")
                .setContentText("A new task has been added to your list.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(1002, notificationBuilder.build())
        } else {
            Toast.makeText(this, "Notification permission is required to send reminders.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkAllTasksCompleted() {
        taskViewModel.taskItems.observe(this) { taskList ->
            val filteredList = when (currentCategory) {
                "Work" -> taskList.filter { it.category == "Work" }
                "Personal" -> taskList.filter { it.category == "Personal" }
                "Wishlist" -> taskList.filter { it.category == "Wishlist" }
                else -> taskList
            }

            val allCompleted = filteredList.all { it.isCompleted }
            if (allCompleted && filteredList.isNotEmpty()) {
                sendAllTasksCompletedNotification(currentCategory)
            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager, "newTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setCompleted(taskItem)
        sendTaskNotification(taskItem.name)
        checkAllTasksCompleted()
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
        checkAllTasksCompleted()
    }

    fun sendTaskNotification(taskName: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationBuilder = NotificationCompat.Builder(this, "TASK_CHANNEL")
                .setSmallIcon(R.drawable.notifications_24)
                .setContentTitle("Task Reminder")
                .setContentText("It's time to complete: $taskName")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 1000))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(1001, notificationBuilder.build())
        } else {
            Toast.makeText(this, "Notification permission is required to send reminders.", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendAllTasksCompletedNotification(category: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationBuilder = NotificationCompat.Builder(this, "TASK_CHANNEL")
                .setSmallIcon(R.drawable.notifications_24)
                .setContentTitle("All Tasks Completed")
                .setContentText("Congratulations! You've completed all tasks in the $category category.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 1000))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(1003, notificationBuilder.build())
        } else {
            Toast.makeText(this, "Notification permission is required to send reminders.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
