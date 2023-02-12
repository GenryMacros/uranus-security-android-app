package com.example.uranus.ui.confirmation

import com.example.uranus.ui.login.LoggedInUserView


data class ConfirmationResult(
    val success: LoggedInUserView? = null,
    val error: String? = null
)