package com.example.todolist.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.Task
import com.example.todolist.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val repository: Repository
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = repository.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
      searchQuery,
        preferencesFlow
    ){ searchQuery, filterPreferences->
        Pair(searchQuery, filterPreferences)

    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query,filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        repository.updateSortOrder(sortOrder)
    }

    fun onHideCompleted(hideCompleted: Boolean) = viewModelScope.launch {
        repository.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task){

    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.updateTask(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.deleteTask(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeletedTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insertTask(task)
    }

    sealed class TaskEvent{
        data class ShowUndoDeletedTaskMessage(val task: Task): TaskEvent()
    }

}

