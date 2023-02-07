package com.example.uranus.ui.signup

/**
 * Data validation state of the login form.
 */
data class SignupFormState(
    var usernameError: Int? = null,
    var passwordError: Int? = null,
    var firstNameError: Int? = null,
    var lastNameError: Int? = null,
    var phoneError: Int? = null,
    var emailError: Int? = null,
    var telegramError: Int? = null,
    var isDataValid: Boolean = false
)