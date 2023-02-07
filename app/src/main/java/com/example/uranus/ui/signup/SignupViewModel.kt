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
        val currentState = SignupFormState(isDataValid = true)
        if (!isUsernameValid(signupData.username)) {
            currentState.isDataValid = false
            currentState.usernameError =R.string.invalid_username
        }
        if (!isPasswordValid(signupData.password)) {
            currentState.isDataValid = false
            currentState.passwordError = R.string.invalid_password
        }
        if (!isFirstNameValid(signupData.first_name)) {
            currentState.isDataValid = false
            currentState.firstNameError = R.string.invalid_first_name
        }
        if (!isLastNameValid(signupData.last_name)) {
            currentState.isDataValid = false
            currentState.lastNameError = R.string.invalid_last_name
        }
        if (!isPhoneValid(signupData.phone)) {
            currentState.isDataValid = false
            currentState.phoneError = R.string.invalid_phone
        }
        if (!isEmailValid(signupData.email)) {
            currentState.isDataValid = false
            currentState.emailError = R.string.invalid_email
        }
        if (!isTelegramValid(signupData.telegram)) {
            currentState.isDataValid = false
            currentState.telegramError = R.string.invalid_telegram
        }
        _signupForm.value = currentState
    }

    // A placeholder username validation check
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

    private fun isTelegramValid(telegram: String): Boolean {
        return telegram.length in 3..20 && telegram[0] == '@'
    }

    private fun isPhoneValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    private fun isFirstNameValid(firstName: String): Boolean {
        return firstName.length in 2..15
    }

    private fun isLastNameValid(lastName: String): Boolean {
        return lastName.length in 4..20
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length in 6..19
    }
}