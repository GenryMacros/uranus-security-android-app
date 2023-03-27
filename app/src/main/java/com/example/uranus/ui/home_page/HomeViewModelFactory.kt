package com.example.uranus.ui.home_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.data.SignupRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class HomeViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                signupRepository = SignupRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}