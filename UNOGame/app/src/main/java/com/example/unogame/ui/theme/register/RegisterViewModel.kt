package com.example.unogame.ui.theme.register

import androidx.lifecycle.ViewModel
import com.example.unogame.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val _registerState = MutableStateFlow<Boolean?>(null)
    val registerState: StateFlow<Boolean?> = _registerState

    fun register(email: String, password: String, username: String) {
        repository.register(email, password, username) { success, _ ->
            _registerState.value = success
        }
    }
}