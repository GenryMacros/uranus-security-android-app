package network.interfaces
import com.example.uranus.ui.signup.interfaces.SignupData
import com.google.gson.annotations.SerializedName


data class SignupDataRequest(
    val username: String,
    val password: String,
    val email: String
) {
    companion object {
        fun createFromUI(uieq: SignupData) : SignupDataRequest {
            return SignupDataRequest(
                username = uieq.username,
                password = uieq.password,
                email = uieq.email
            )
        }
    }
}


data class SignupResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("reason")
    val reason: String? = null
)
