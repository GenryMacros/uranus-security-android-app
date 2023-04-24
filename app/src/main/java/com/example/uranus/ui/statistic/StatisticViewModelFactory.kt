package com.example.uranus.ui.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.data.StatisticsRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class StatisticViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(
                statisticsRepository = StatisticsRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}