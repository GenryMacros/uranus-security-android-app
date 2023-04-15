package com.example.uranus.ui.invasions_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.data.InvasionsRepository
import com.example.uranus.data.SignupRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class InvasionsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvasionsViewModel::class.java)) {
            return InvasionsViewModel(
                invasionsRepository = InvasionsRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}