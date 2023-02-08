package com.example.uranus.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

import com.example.uranus.R
import com.example.uranus.data.SignupRepository
import com.example.uranus.ui.signup.interfaces.SignupData


class SignupViewModel(private val signupRepository: SignupRepository) : ViewModel() {

    private val _signupForm = MutableLiveData<SignupFormState>()
    val signupFormState: LiveData<SignupFormState> = _signupForm

    private val _signupResult = MutableLiveData<SignupResult>()
    val signupResult: LiveData<SignupResult> = _signupResult

    fun signup(signupData: SignupData) {
        signupRepository.signup(signupData, _signupResult)
    }

    fun signupDataChanged(signupData: SignupData) {
        var isDataValid = true
        var usernameError: Int? = null
        var passwordError: Int? = null
        var emailError: Int? = null

        if (!isUsernameValid(signupData.username)) {
            isDataValid = false
            usernameError = R.string.invalid_username
        }
        if (!isPasswordValid(signupData.password)) {
            isDataValid = false
            passwordError = R.string.invalid_password
        }
        if (!isEmailValid(signupData.email)) {
            isDataValid = false
            emailError = R.string.invalid_email
        }
        _signupForm.value = SignupFormState(
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            isDataValid = isDataValid)
    }

    private fun isUsernameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length in 6..19
    }
}
