package com.example.skipapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BaseViewModel<T>(initial: T) : ViewModel() {
    protected val _state: MutableStateFlow<T> = MutableStateFlow(initial)
    val state: StateFlow<T> get() = _state
}
