package com.example.uranus.ui.home_page.data

class GetCamerasResponse(
    val cameras: List<CamData>,
    val success: Boolean
)

class CamData(
    val cam_name: Int,
    val cam_id: Int,
    val is_online: Boolean
)