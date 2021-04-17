package com.example.todolist.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.todolist.data.Task
import com.example.todolist.data.TaskDao
import com.example.todolist.ui.ADD_TASK_RESULT_OK
import com.example.todolist.ui.EDIT_TASK_RESULT_OK
import com.example.todolist.ui.addedittask.AddEditTaskViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val repository: Repository,
    @Assisted private val state: SavedStateHandle
): ViewModel() {

    val searchQuery = state.getLiveData("searchQuery","")

    val preferencesFlow = repository.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
      searchQuery.asFlow(),
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

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
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

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int){
        when(result){
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(message: String) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(message))
    }

    sealed class TaskEvent{
        object NavigateToAddTaskScreen: TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task): TaskEvent()
        data class ShowUndoDeletedTaskMessage(val task: Task): TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val message: String): TaskEvent()
    }



}

