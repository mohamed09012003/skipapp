package com.example.skipapp.ui

import com.example.skipapp.viewmodel.BaseViewModel

data class MainUiState(val message: String = "Welcome to SkipApp")

class MainViewModel : BaseViewModel<MainUiState>(MainUiState()) {
    fun setMessage(text: String) {
        _state.value = _state.value.copy(message = text)
    }
}
