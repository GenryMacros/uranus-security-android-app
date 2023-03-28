package com.example.uranus.ui.home_page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.client.IO
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mSocket: Socket;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = binding.profile
        val addDevice = binding.add
        val settings = binding.settings

        homeViewModel = ViewModelProvider(this, HomeViewModelFactory())
            .get(HomeViewModel::class.java)

        try {
            mSocket = IO.socket("http://10.0.2.2:8086")
            Log.d("success", "Connected to socket server")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        mSocket.on(Socket.EVENT_CONNECT, onFrames)
        mSocket.on("ROBBERY", onRobbery)
        mSocket.connect()
    }


    var onFrames = Emitter.Listener {

    }


    var onRobbery = Emitter.Listener {
        Log.d("fail", "ROBBERY")
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