package com.example.uranus.ui.invasions_page.data

import network.api.interfaces.InvasionIn

class Invasion(
    var id: Int,
    var date: Int,
    var link: String,
    var link_short: String
)

class InvasionsData(
    var invasions: List<InvasionIn>? = null,
    var success: Boolean? = false,
    var reason: String? = null
)
