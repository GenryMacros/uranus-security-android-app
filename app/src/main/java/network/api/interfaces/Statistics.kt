package network.api.interfaces

import com.google.gson.annotations.SerializedName


data class StatisticsGetOut(
    val auth_token: String,
    val cam_id: Int,
    val client_id: Int,
    val date: Int
)


data class StatisticsGetResponse(
    @SerializedName("latest")
    val latest: Int? = null,
    @SerializedName("intruders")
    val intruders: Int? = null,
    @SerializedName("duration")
    val duration: Int? = null,
    @SerializedName("invasions")
    val invasions: Int? = null,
    @SerializedName("reason")
    val reason: String? = null,
    @SerializedName("success")
    val success: Boolean
)
