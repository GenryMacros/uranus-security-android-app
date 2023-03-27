package com.example.uranus.ui.home_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

import com.example.uranus.R
import com.example.uranus.data.SignupRepository
import com.example.uranus.ui.signup.interfaces.SignupData


class HomeViewModel(private val signupRepository: SignupRepository) : ViewModel() {

   // private val _signupForm = MutableLiveData<SignupFormState>()
   // val signupFormState: LiveData<SignupFormState> = _signupForm

    //private val _signupResult = MutableLiveData<SignupResult>()
    //val signupResult: LiveData<SignupResult> = _signupResult

    fun signup(signupData: SignupData) {
        //signupRepository.signup(signupData, _signupResult)
    }

}
