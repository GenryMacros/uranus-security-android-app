package network.api.interfaces

import com.google.gson.annotations.SerializedName



data class RefreshRequest(
    @SerializedName("user_id")
    val id: Int? = null,
    @SerializedName("jwt")
    val token: String? = null,
    @SerializedName("refresh")
    val refresh: String? = null
)

data class RefreshResponse(
    @SerializedName("user_id")
    val id: Int? = null,
    @SerializedName("jwt")
    val jwt: String? = null,
    @SerializedName("refresh")
    val refresh: String? = null,
    @SerializedName("reason")
    val reason: String? = null,
    @SerializedName("success")
    val success: Boolean? = null
)
