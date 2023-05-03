package com.example.uranus.ui.invasions_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uranus.data.InvasionsRepository
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.example.uranus.ui.invasions_page.data.InvasionsData
import network.api.interfaces.InvasionGetOut

class InvasionsViewModel(private val invasionsRepository: InvasionsRepository) : ViewModel() {

    private val _invasions = MutableLiveData<InvasionsData>()
    val invasions: LiveData<InvasionsData> = _invasions

    private val _is_updated = MutableLiveData<Boolean>()
    val is_updated: LiveData<Boolean> = _is_updated

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun getInvasions(authData: AuthenticationData, camId: Int) {
        val requestData = InvasionGetOut(
            auth_token=authData.token,
            client_id=authData.userId,
            cam_id=camId,
            date=0,
            refresh=authData.refreshToken
        )
        invasionsRepository.getInvasions(requestData, _invasions, _is_updated, _token)
    }
}
