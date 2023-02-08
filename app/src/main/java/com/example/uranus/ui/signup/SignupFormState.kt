package com.example.uranus.ui.signup


data class SignupFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val emailError: Int? = null,
    val isDataValid: Boolean = false
)
