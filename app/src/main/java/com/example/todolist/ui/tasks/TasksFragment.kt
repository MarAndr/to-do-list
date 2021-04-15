package com.example.todolist.ui.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ViewBindingFragment
import com.example.todolist.R
import com.example.todolist.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment: ViewBindingFragment<FragmentTasksBinding>(FragmentTasksBinding::inflate) {

    private val viewModel: TasksViewModel by viewModels()
}