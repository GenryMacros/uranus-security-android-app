package com.example.uranus.data

import androidx.lifecycle.MutableLiveData
import com.beust.klaxon.Klaxon
import com.example.uranus.ui.statistic.data.StatisticsData
import network.api.MainServerApi
import network.api.interfaces.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatisticsRepository {
    fun getStatistics(request_data: StatisticsGetOut, statisticsData: MutableLiveData<StatisticsData>,
                      updatedData: MutableLiveData<Boolean>
    ) {
        val apiRequestResult = MainServerApi.getApi()?.getInvasionStatistics(request_data)
        apiRequestResult?.enqueue(object: Callback<StatisticsGetResponse> {
            override fun onFailure(call: Call<StatisticsGetResponse>, t: Throwable) {
                statisticsData.value = StatisticsData(
                    success = false,
                    reason = "Can't establish connection with server")
            }

            override fun onResponse(
                call: Call<StatisticsGetResponse>,
                response: Response<StatisticsGetResponse>
            ) {
                val responseObj = response.body()
                if (responseObj != null) {
                    if (responseObj.reason != null) {
                        statisticsData.value = StatisticsData(reason = responseObj.reason)
                    } else {
                        statisticsData.value = StatisticsData(
                            success = responseObj.success,
                            invasions = responseObj.invasions,
                            latest = responseObj.latest,
                            intruders = responseObj.intruders,
                            duration = responseObj.duration,
                            reason = responseObj.reason)
                        updatedData.value = true
                    }
                } else {
                    if (response.errorBody() == null) {
                        statisticsData.value = StatisticsData(reason = "Server was corrupted")
                    } else {
                        var errorBody = response.errorBody()!!.byteStream().toString()
                        errorBody = errorBody.replace("[text=", "")
                            .replace("\\n].inputStream()", "")
                        val loginResponse = Klaxon().parse<LoginResponse>(errorBody)
                        if (loginResponse != null) {
                            statisticsData.value = StatisticsData(reason = loginResponse.reason)
                        } else {
                            statisticsData.value = StatisticsData(reason = "Unknown server error")
                        }
                    }
                }
            }
        })
    }
}
