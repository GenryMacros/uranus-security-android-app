package com.example.uranus.services
import org.json.JSONObject

enum class InEvent { ASK_AUTHENTICATE, FRAMES }

enum class EventType {
    GET_CAMERAS,
    AUTHENTICATE,
    READ_FRAMES,
    ASK_AUTHENTICATE,
    INVASION,
    FRAMES,
    ERROR
}

class SocketEvent(
    val type: EventType,
    val body: JSONObject?
)
