package com.studentsecurevpn.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studentsecurevpn.util.ServiceLocator
import kotlinx.coroutines.launch

/** Login ViewModel keeps network work out of the Activity. */
class LoginViewModel : ViewModel() {
    private val authRepository = ServiceLocator.authRepository
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> = _success

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = authRepository.login(email, password)
            _loading.value = false
            result.onSuccess { _success.value = true }
                .onFailure { _error.value = it.message ?: "Login failed" }
        }
    }

    fun guest() {
        authRepository.continueAsGuest()
        _success.value = true
    }
}
