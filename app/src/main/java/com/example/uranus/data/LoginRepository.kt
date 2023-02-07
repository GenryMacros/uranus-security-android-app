package com.example.uranus.data

import androidx.lifecycle.MutableLiveData
import com.example.uranus.ui.login.LoggedInUserView
import com.example.uranus.ui.login.LoginResult
import com.example.uranus.utils.SecretsHandler
import network.MainServerApi
import network.interfaces.LoginData
import network.interfaces.LoginResponse
import network.interfaces.LoginResponseView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoginResponse? = null
        private set

    var secretsHandler: SecretsHandler = SecretsHandler()

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

    fun login(username: String, password: String, loginResult:
    MutableLiveData<LoginResult>
    ) {
        val apiRequestResult = MainServerApi.getApi()?.login(LoginData(username, password))
        apiRequestResult?.enqueue(object: Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = LoginResult(error = "Can't establish connection with server")
            }

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val responseObj = response.body()
                if (responseObj?.reason != null) {
                    loginResult.value = LoginResult(error = responseObj.reason)
                } else {
                    val parsedResponse = secretsHandler.parseToken(responseObj?.auth_token ?: "",
                        responseObj?.public_key ?: "")
                    if (parsedResponse == null) {
                        loginResult.value = LoginResult(error = "Server was corrupted")
                    } else {
                        user = responseObj
                        val displayName = "${parsedResponse.first_name} ${parsedResponse.last_name}"
                        LoginResponseView(displayName = displayName)
                        loginResult.value = LoginResult(success = LoggedInUserView(displayName = displayName))
                    }
                }
            }
        })
    }

}