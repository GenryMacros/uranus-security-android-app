package com.example.uranus.ui.home_page

data class HomeAuthData(
    val public_key: String? = null,
    val auth_token: String? = null,
    val refresh_token: String? = null,
    val user_id: Int? = null
)
