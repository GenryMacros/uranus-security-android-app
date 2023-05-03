package com.example.uranus.ui.home_page

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import com.example.uranus.R
import com.example.uranus.databinding.ActivityHomeBinding
import com.example.uranus.services.EventType
import com.example.uranus.services.SocketEvent
import com.example.uranus.services.SocketService
import com.example.uranus.services.data.ReauthData
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.example.uranus.ui.home_page.data.GetCamerasResponse
import com.example.uranus.ui.home_page.utility.CamsHandler
import com.example.uranus.ui.statistic.StatisticActivity
import com.google.gson.Gson


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var camsHandler: CamsHandler
    private lateinit var authData: AuthenticationData
    private var gson = Gson()
    private var mService: SocketService = SocketService()
    var mBound = false

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: SocketService.SocketBinder = service as SocketService.SocketBinder
            mService = binder.getService()
            mService.connect(authData)

            mService.isAuthenticated.observe(this@HomeActivity, Observer {
                val isAuthenticated = it ?: return@Observer
                if (isAuthenticated) {
                    mService.sendEvent(SocketEvent(EventType.GET_CAMERAS, null))
                }
            })

            mService.serverReceivedEvents.observe(this@HomeActivity, Observer {
                val events = it ?: return@Observer
                events.forEach { event ->
                    run {
                        when (event.type) {
                            EventType.GET_CAMERAS -> {
                                val responseObj: GetCamerasResponse = gson.fromJson(event.body.toString(),
                                    GetCamerasResponse::class.java)

                                if (responseObj.success) {
                                    camsHandler.setCamData(responseObj.cameras)
                                }
                            }
                            EventType.REAUTH_HAPPENED -> {
                                val reauthObj: ReauthData = gson.fromJson(event.body.toString(),
                                    ReauthData::class.java)
                                authData.token = reauthObj.new_token
                            }
                            else -> {}
                        }
                        mService.removeObservedEvent(event)
                    }
                }
            })

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authData = createAuthData()
        val intent1 = Intent(this, SocketService::class.java)
        bindService(intent1, mConnection, Context.BIND_AUTO_CREATE)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        camsHandler = CamsHandler(this,
                                 findViewById(R.id.cameras_container),
                                 authData)

        val statsButton = binding.stats
        val loadingBar = binding.loading

        statsButton.setOnClickListener {
            StatisticActivity.startActivity(this, authData)
        }

        camsHandler.isNeedRefresh.observe(this@HomeActivity, Observer {
            val isNeedRefresh = it ?: return@Observer
            if (isNeedRefresh) {
                camsHandler.generateButtons()
                loadingBar.visibility = View.GONE
            }
        })

    }

    private fun createAuthData(): AuthenticationData {
        val publicKey = intent.getStringExtra("public_key")
        val token = intent.getStringExtra("auth_token")
        val refreshToken = intent.getStringExtra("refresh_token")
        val userId = intent.getIntExtra("user_id", -1)

        return AuthenticationData(publicKey ?: "",
                                  token ?: "",
                                  refreshToken ?: "",
                                  userId)
    }

    companion object {
        fun startActivity(context: Context, login_data: HomeAuthData) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("public_key", login_data.public_key)
            intent.putExtra("auth_token", login_data.auth_token)
            intent.putExtra("refresh_token", login_data.refresh_token)
            intent.putExtra("user_id", login_data.user_id)
            context.startActivity(intent)
        }
    }
}
