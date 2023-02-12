package com.example.uranus.ui.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

import com.example.uranus.R
import com.example.uranus.data.ConfirmationRepository
import com.example.uranus.data.SignupRepository
import com.example.uranus.ui.confirmation.interfaces.ConfirmationData
import com.example.uranus.ui.signup.interfaces.SignupData


class ConfirmationViewModel(private val confirmationRepository: ConfirmationRepository) : ViewModel() {

    private val _confirmationForm = MutableLiveData<ConfirmationFormState>()
    val confirmationFormState: LiveData<ConfirmationFormState> = _confirmationForm

    private val _confirmationResult = MutableLiveData<ConfirmationResult>()
    val confirmationResult: LiveData<ConfirmationResult> = _confirmationResult

    fun confirm(confirmationData: ConfirmationData) {
        confirmationRepository.confirm(confirmationData, _confirmationResult)
    }

    fun confirmationDataChanged(confirmationData: ConfirmationData) {
        var isDataValid = true
        var tokenError: Int? = null

        if (!isTokenValid(confirmationData.token)) {
            isDataValid = false
            tokenError = R.string.invalid_token
        }
        _confirmationForm.value = ConfirmationFormState(
            tokenError=tokenError,
            isDataValid = isDataValid)
    }

    private fun isTokenValid(token: String): Boolean {
        return true
    }
}
