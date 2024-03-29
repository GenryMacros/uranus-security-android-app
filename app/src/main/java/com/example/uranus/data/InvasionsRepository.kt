package com.example.uranus.data

import androidx.lifecycle.MutableLiveData
import com.beust.klaxon.Klaxon
import com.example.uranus.ui.invasions_page.data.InvasionsData
import network.api.MainServerApi
import network.api.interfaces.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvasionsRepository {

    fun getInvasions(request_data: InvasionGetOut, invasionsData: MutableLiveData<InvasionsData>,
                     updatedData: MutableLiveData<Boolean>, updatedToken: MutableLiveData<String>
    ) {
        val apiRequestResult = MainServerApi.getApi()?.getInvasions(request_data)
        apiRequestResult?.enqueue(object: Callback<InvasionGetResponse> {
            override fun onFailure(call: Call<InvasionGetResponse>, t: Throwable) {
                invasionsData.value = InvasionsData(
                    success = false,
                    reason = "Can't establish connection with server")
            }

            override fun onResponse(
                call: Call<InvasionGetResponse>,
                response: Response<InvasionGetResponse>
            ) {
                val responseObj = response.body()
                if (responseObj != null) {
                    if (responseObj.reason != null) {
                        if (responseObj.reason == "Token is expired") {
                            reauth(RefreshRequest(
                                id=request_data.client_id,
                                token=request_data.auth_token,
                                refresh=request_data.refresh
                            ),updatedToken)
                        } else {
                            invasionsData.value = InvasionsData(reason = responseObj.reason)
                        }
                    } else {
                        invasionsData.value = InvasionsData(
                                    success = responseObj.success,
                                    invasions = responseObj.invasions,
                                    reason = responseObj.reason)
                        updatedData.value = true
                    }
                } else {
                    if (response.errorBody() == null) {
                        invasionsData.value = InvasionsData(reason = "Server was corrupted")
                    } else {
                        var errorBody = response.errorBody()!!.byteStream().toString()
                        errorBody = errorBody.replace("[text=", "")
                            .replace("\\n].inputStream()", "")
                        val loginResponse = Klaxon().parse<LoginResponse>(errorBody)
                        if (loginResponse != null) {
                            invasionsData.value = InvasionsData(reason = loginResponse.reason)
                        } else {
                            invasionsData.value = InvasionsData(reason = "Unknown server error")
                        }
                    }
                }
            }
        })
    }

    fun reauth(request_data: RefreshRequest, updatedToken: MutableLiveData<String>) {
        val apiRequestResult = MainServerApi.getApi()?.refresh(request_data)
        apiRequestResult?.enqueue(object: Callback<RefreshResponse> {
            override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {}
            override fun onResponse(
                call: Call<RefreshResponse>,
                response: Response<RefreshResponse>
            ) {
                val responseObj = response.body()
                if (responseObj != null) {
                    if (responseObj.reason == null) {
                        updatedToken.value = responseObj.jwt
                    }
                }
            }
        })
    }

}