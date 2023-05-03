package com.example.uranus.services
import org.json.JSONObject

enum class EventType {
    GET_CAMERAS,
    AUTHENTICATE,
    READ_FRAMES,
    ASK_AUTHENTICATE,
    INVASION,
    REAUTH_HAPPENED,
    FRAMES,
    ERROR
}

class SocketEvent(
    val type: EventType,
    val body: JSONObject?
)
