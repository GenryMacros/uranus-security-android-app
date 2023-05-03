package com.example.uranus.ui.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uranus.data.StatisticsRepository
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.example.uranus.ui.statistic.data.StatisticsData
import network.api.interfaces.StatisticsGetOut

class StatisticsViewModel(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private val _statisticsData = MutableLiveData<StatisticsData>()
    val statisticsData: LiveData<StatisticsData> = _statisticsData

    private val _is_updated = MutableLiveData<Boolean>()
    val is_updated: LiveData<Boolean> = _is_updated

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun getStatistic(authData: AuthenticationData, camId: Int) {
        val requestData = StatisticsGetOut(
            auth_token=authData.token,
            client_id=authData.userId,
            cam_id=camId,
            refresh=authData.refreshToken,
            date=0
        )
        statisticsRepository.getStatistics(requestData,
                                           _statisticsData,
                                           _is_updated,
                                           _token)
    }
}
