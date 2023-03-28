package com.example.uranus.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.uranus.data.LoginRepository

import com.example.uranus.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        loginRepository.login(username, password, _loginResult)
    }

    fun loginDataChanged(username: String, password: String) {
        var isDataValid = true
        var usernameError: Int? = null
        var passwordError: Int? = null

        if (!isUserNameValid(username)) {
            isDataValid = false
            usernameError = R.string.invalid_username
        }
        if (!isPasswordValid(password)) {
            isDataValid = false
            passwordError = R.string.invalid_password
        }
        _loginForm.value = LoginFormState(
            usernameError=usernameError,
            passwordError=passwordError,
            isDataValid=isDataValid
        )
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}