package com.example.uranus.data

import androidx.lifecycle.MutableLiveData
import com.example.uranus.ui.signup.SignupResult
import com.example.uranus.ui.signup.interfaces.SignupData
import network.MainServerApi
import network.interfaces.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupRepository {


    fun signup(signupData: SignupData, signupResult: MutableLiveData<SignupResult>
    ) {
        val apiRequestResult = MainServerApi.getApi()?.signup(SignupDataRequest.createFromUI(signupData))
        apiRequestResult?.enqueue(object: Callback<SignupResponse> {
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                signupResult.value = SignupResult(error = "Can't establish connection with server")
            }

            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                val responseObj = response.body()
                if (responseObj?.reason != null) {
                    signupResult.value = SignupResult(error = responseObj.reason)
                } else {
                    if (responseObj != null) {
                        if (!responseObj.success) {
                            signupResult.value = SignupResult(error = "Signup was unsuccessful")
                        } else {
                            signupResult.value = SignupResult(error = null)
                        }
                    } else {
                        signupResult.value = SignupResult(error = "Server was corrupted")
                    }
                }
            }
        })
    }
}
