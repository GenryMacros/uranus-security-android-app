package com.example.uranus.ui.confirmation

import com.example.uranus.ui.login.LoggedInUserView


data class ConfirmationResult(
    val success: LoggedInUserView? = null,
    val error: String? = null,
    val public_key: String? = null,
    val auth_token: String? = null,
    val refresh_token: String? = null,
    val user_id: Int? = null
)