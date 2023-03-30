package com.example.uranus.ui.home_page.data

class GetCamerasResponse(
    val cameras: List<CamData>,
    val success: Boolean
)

class CamData(
    val camId: Int,
    val isOnline: Boolean
)