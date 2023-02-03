package network.interfaces
import com.google.gson.annotations.SerializedName

data class LoginData(
    val username: String,
    val password: String)


data class LoginResponseView(
    val displayName: String = "",
    val error: String? = null
)

data class LoginResponse(
    @SerializedName("user_id")
    val id: Int? = null,
    @SerializedName("user_name")
    val public_key: String? = null,
    @SerializedName("auth_token")
    val auth_token: String? = null,
    @SerializedName("refresh_token")
    val refresh_token: String? = null,
    @SerializedName("reason")
    val reason: String? = null
)

data class LoginResponseBody(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val expiration_date: Int
)
