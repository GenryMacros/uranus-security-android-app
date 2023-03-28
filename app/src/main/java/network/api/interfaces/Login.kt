package network.api.interfaces
import com.google.gson.annotations.SerializedName

data class LoginData(
    val login: String,
    val password: String)


data class LoginResponseView(
    val displayName: String = "",
    val error: String? = null
)

data class LoginResponse(
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

data class LoginResponseBody(
    val id: Int,
    val email: String,
    val expiration_date: Float
)
