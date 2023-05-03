package network.api.interfaces

import com.google.gson.annotations.SerializedName


data class InvasionGetOut(
    val auth_token: String,
    val cam_id: Int,
    val client_id: Int,
    val date: Int,
    val refresh: String
    )

data class InvasionIn(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("date")
    val date: Int? = null,
    @SerializedName("file_name")
    val file_name: String? = null,
    @SerializedName("link")
    val link: String? = null,
    @SerializedName("link_short")
    val link_short: String? = null,
    @SerializedName("invaders")
    val invaders: List<String>? = null
)

data class InvasionGetResponse(
    @SerializedName("invasions")
    val invasions: List<InvasionIn>? = null,
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("reason")
    val reason: String? = null
)
