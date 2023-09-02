package com.example.recovery.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SplashViewModel :ViewModel() {
    val isReadyForExit = MutableStateFlow(true)
    init {
        viewModelScope.launch {
            delay(1500)
            isReadyForExit.tryEmit(false)
        }
    }
}