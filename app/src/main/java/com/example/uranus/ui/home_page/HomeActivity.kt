package com.example.uranus.ui.home_page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.R
import com.example.uranus.databinding.ActivityHomeBinding
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.example.uranus.ui.home_page.data.AuthenticationResponse
import com.example.uranus.ui.home_page.data.GetCamerasResponse
import com.example.uranus.ui.home_page.utility.CamsHandler
import com.example.uranus.ui.login.LoginActivity
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mSocket: Socket;
    private lateinit var camsHandler: CamsHandler;
    private var gson = Gson();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        camsHandler = CamsHandler(this,
                                 findViewById(R.id.cameras_container))

        val profile = binding.profile
        val addDevice = binding.add
        val settings = binding.settings

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory())
            .get(HomeViewModel::class.java)

        camsHandler.isNeedRefresh.observe(this@HomeActivity, Observer {
            val isNeedRefresh = it ?: return@Observer
            if (isNeedRefresh) {
                camsHandler.generateButtons()
            }
        })

        try {
            mSocket = IO.socket("http://10.0.2.2:8086")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        mSocket.on(Socket.EVENT_CONNECT, onFrames)
        mSocket.on("ROBBERY", onRobbery)
        mSocket.connect()
        mSocket.emit("authenticate", JSONObject(gson.toJson(createAuthData())),
            Ack { args -> authenticateCallback(args) });
    }


    var onFrames = Emitter.Listener {

    }

    var onRobbery = Emitter.Listener {
        Log.d("fail", "ROBBERY")
    }

    private fun authenticateCallback(vararg args: Any) {
        val response: JSONObject = (args[0] as Array<Any>)[0] as JSONObject
        val responseObj: AuthenticationResponse = gson.fromJson(response.toString(),
                                                                AuthenticationResponse::class.java)
        if (responseObj.success) {
            mSocket.emit("get_cameras", Ack { args -> getCamerasCallback(args) })
        } else {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun getCamerasCallback(vararg args: Any) {
        val response: JSONObject = (args[0] as Array<Any>)[0] as JSONObject
        val responseObj: GetCamerasResponse = gson.fromJson(response.toString(),
            GetCamerasResponse::class.java)

        if (responseObj.success) {
            camsHandler.setCamData(responseObj.cameras)
        }
    }

    private fun createAuthData(): AuthenticationData {
        val publicKey = intent.getStringExtra("public_key")
        val token = intent.getStringExtra("auth_token")
        val refreshToken = intent.getStringExtra("refresh_token")
        val userId = intent.getIntExtra("user_id", -1)

        return AuthenticationData(publicKey,
                                  token,
                                  refreshToken,
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

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}