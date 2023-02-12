package com.example.uranus.ui.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.data.ConfirmationRepository

class ConfirmationViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfirmationViewModel::class.java)) {
            return ConfirmationViewModel(
                confirmationRepository = ConfirmationRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}