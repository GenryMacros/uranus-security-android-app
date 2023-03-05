package network.interfaces

import com.example.uranus.ui.confirmation.interfaces.ConfirmationData
import com.google.gson.annotations.SerializedName

class ConfirmationDataRequest(
    val token: String,
    val id: Int
) {
    companion object {
        fun createFromUI(uieq: ConfirmationData) : ConfirmationDataRequest {
            return ConfirmationDataRequest(
                token = uieq.token,
                id=uieq.id
            )
        }
    }
}


data class ConfirmationResponse(
    @SerializedName("user_id")
    val id: Int? = null,
    @SerializedName("public_key")
    val public_key: String? = null,
    @SerializedName("auth_token")
    val auth_token: String? = null,
    @SerializedName("refresh_token")
    val refresh_token: String? = null,
    @SerializedName("reason")
    val reason: String? = null,
    @SerializedName("success")
    val success: Boolean? = null
)
