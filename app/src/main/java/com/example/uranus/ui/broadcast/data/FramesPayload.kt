package com.example.uranus.ui.invasions_page.data


class CamFrame(
    val buffer: String,
    val id: Int
)

class FramesPayload(
    val frames: Array<CamFrame>
)
