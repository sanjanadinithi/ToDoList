package com.example.todolist

import android.content.Context
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TaskItem(
    var name: String,
    var dueTime: LocalTime?,
    var dueDate: LocalDate? = null,
    var completedDate: LocalDate? = null,
    var category: String? = "Default Category",
    var id: UUID = UUID.randomUUID()
) {
    val isCompleted: Boolean
        get() = completedDate != null

    fun imageResource(): Int = if (isCompleted) R.drawable.checked_24 else R.drawable.unchecked_24
    fun imageColor(context: Context): Int = if (isCompleted) purple(context) else black(context)

    private fun purple(context: Context) = ContextCompat.getColor(context, R.color.purple_500)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black)
}
