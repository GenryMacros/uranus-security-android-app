package com.example.uranus.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: String? = null,
    val public_key: String? = null,
    val auth_token: String? = null,
    val refresh_token: String? = null,
    val user_id: Int? = null
)
