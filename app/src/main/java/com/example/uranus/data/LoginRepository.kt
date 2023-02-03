package com.example.uranus.data

import com.example.uranus.R
import com.example.uranus.data.model.LoggedInUser
import com.example.uranus.ui.login.LoggedInUserView
import com.example.uranus.ui.login.LoginResult
import com.example.uranus.ui.login.LoginViewModel
import network.MainServerApi
import network.interfaces.LoginData
import network.interfaces.LoginResponse
import network.interfaces.LoginResponseView
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoginResponse? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        //dataSource.logout()
    }

    fun login(username: String, password: String): LoginResponseView {
        try {
            val apiRequestResult = MainServerApi.getApi()?.login(LoginData(username, password))
            if (apiRequestResult != null) {
                return if (apiRequestResult.isSuccess) {
                    val requestResult =
                        apiRequestResult.getOrDefault(LoginResponse(reason="Can't establish connection with server"))
                    if (requestResult.reason != null) {
                        LoginResponseView(error = requestResult.reason)
                    } else {
                        this.user = requestResult
                        LoginResponseView(displayName = "NONE")
                    }

                } else {
                    LoginResponseView(error = "Can't establish connection with server")
                }
            } else {
                return LoginResponseView(error ="Can't establish connection with server")
            }
        } catch (e: Throwable) {
            return LoginResponseView(error = "Can't establish connection with server")
        }
    }

}