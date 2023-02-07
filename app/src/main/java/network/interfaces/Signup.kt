package network.interfaces
import com.example.uranus.ui.signup.interfaces.SignupData
import com.google.gson.annotations.SerializedName


data class SignupDataRequest(
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val telegram: String
) {
    companion object {
        fun createFromUI(uieq: SignupData) : SignupDataRequest {
            return SignupDataRequest(
                username = uieq.username,
                password = uieq.password,
                first_name = uieq.first_name,
                last_name = uieq.last_name,
                email = uieq.email,
                phone = uieq.phone,
                telegram = uieq.telegram
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
