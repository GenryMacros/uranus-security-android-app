package com.example.uranus.data

import androidx.lifecycle.MutableLiveData
import com.beust.klaxon.Klaxon
import com.example.uranus.ui.confirmation.ConfirmationResult
import com.example.uranus.ui.confirmation.interfaces.ConfirmationData
import com.example.uranus.ui.login.LoggedInUserView
import com.example.uranus.utils.SecretsHandler
import network.MainServerApi
import network.interfaces.ConfirmationDataRequest
import network.interfaces.LoginResponse
import network.interfaces.LoginResponseView
import network.interfaces.SignupResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmationRepository{
    var secretsHandler: SecretsHandler = SecretsHandler()

    fun confirm(confiramationData: ConfirmationData, confirmationResult: MutableLiveData<ConfirmationResult>)
    {
        val apiRequestResult = MainServerApi.getApi()?.confirm(confiramationData.id,
                                                               confiramationData.token)
        apiRequestResult?.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                confirmationResult.value = ConfirmationResult(error = "Can't establish connection with server")
            }

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val responseObj = response.body()
                if (responseObj?.reason != null) {
                    confirmationResult.value = ConfirmationResult(error = responseObj.reason)
                } else {
                    if (responseObj != null) {
                        val parsedResponse = secretsHandler.parseToken(
                            responseObj.auth_token ?: "",
                            responseObj.public_key ?: ""
                        )
                        if (parsedResponse == null) {
                            confirmationResult.value = ConfirmationResult(error = "Server was corrupted")
                        } else {
                            val displayName = parsedResponse.email
                            LoginResponseView(displayName = displayName)
                            confirmationResult.value =
                                ConfirmationResult(success = LoggedInUserView(displayName = displayName),
                                                   public_key = responseObj.public_key,
                                                   auth_token = responseObj.auth_token,
                                                   refresh_token = responseObj.refresh_token,
                                                   user_id = responseObj.id)
                        }
                    } else {
                        if (response.errorBody() == null) {
                            confirmationResult.value = ConfirmationResult(error = "Server was corrupted")
                        } else {
                            var errorBody = response.errorBody()!!.byteStream().toString()
                            errorBody = errorBody.replace("[text=", "")
                                .replace("\\n].inputStream()", "")
                            val signupResponse = Klaxon().parse<SignupResponse>(errorBody)
                            if (signupResponse != null) {
                                confirmationResult.value = ConfirmationResult(error = signupResponse.reason)
                            } else {
                                confirmationResult.value = ConfirmationResult(error = "Unknown server error")
                            }
                        }
                    }
                }
            }
        })
    }
}