package com.example.unogame.ui.theme.login

import androidx.lifecycle.ViewModel
import com.example.unogame.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState

    fun login(email: String, password: String) {
        repository.login(email, password) { success, _ ->
            _loginState.value = success
        }
    }
}