package com.example.uranus.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.example.uranus.ui.home_page.data.AuthenticationResponse
import com.example.uranus.ui.home_page.data.GetCamerasResponse
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class SocketService : Service() {
    private var gson = Gson();
    private val mBinder: IBinder = SocketBinder()
    private val _serverReceivedEvents = MutableLiveData<MutableList<SocketEvent>?>();
    var serverReceivedEvents: LiveData<MutableList<SocketEvent>?> = _serverReceivedEvents;
    private val _isAuthenticated = MutableLiveData<Boolean>();
    var isAuthenticated: LiveData<Boolean> = _isAuthenticated;

    private lateinit var mSocket: Socket;
    private lateinit var authData: AuthenticationData;

    @Override
    override fun onCreate() {
    }

    fun connect(auth_data: AuthenticationData) {
        authData = auth_data;
        if (!mSocket.connected()) {
            _isAuthenticated.postValue(false)
            mSocket.connect()
        }
    }


    override fun onBind(intent: Intent): IBinder {
        try {
            mSocket = IO.socket("http://10.0.2.2:8086")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (_serverReceivedEvents.value == null) {
            _serverReceivedEvents.postValue(mutableListOf<SocketEvent>())
        }
        mSocket.on(EventType.INVASION.toString().lowercase(), onInvasion)
        mSocket.on(EventType.ASK_AUTHENTICATE.toString().lowercase(), onAuthAsk)
        _isAuthenticated.postValue(_isAuthenticated.value)
        return mBinder
    }

    var onFrames = Emitter.Listener {

    }

    private var onInvasion = Emitter.Listener {
        Log.d("fail", "INVASION")
    }

    private var onAuthAsk = Emitter.Listener {
        mSocket.emit(EventType.AUTHENTICATE.toString().lowercase(), JSONObject(gson.toJson(authData)),
            Ack { args -> authenticateCallback(args) });
    }

    fun removeObservedEvent(event: SocketEvent) {
        _serverReceivedEvents.value?.remove(event);
    }

    fun sendEvent(event: SocketEvent) {
        when(event.type) {
            EventType.GET_CAMERAS -> {
                mSocket.emit(EventType.GET_CAMERAS.toString().lowercase(),
                    Ack { args -> getCamerasCallback(args) })
            }
            EventType.READ_FRAME -> {

            }
            EventType.AUTHENTICATE -> {
                mSocket.emit(EventType.AUTHENTICATE.toString().lowercase(), event.body,
                    Ack { args -> authenticateCallback(args) });
            }
            else -> {}
        }
    }

    private fun getCamerasCallback(vararg args: Any) {
        val response: JSONObject = (args[0] as Array<Any>)[0] as JSONObject
        val updated = _serverReceivedEvents.value;
        updated?.add(SocketEvent(EventType.GET_CAMERAS, response))
        _serverReceivedEvents.postValue(updated)
    }

    private fun authenticateCallback(vararg args: Any) {
        val response: JSONObject = (args[0] as Array<Any>)[0] as JSONObject
        val responseObj: AuthenticationResponse = gson.fromJson(response.toString(),
            AuthenticationResponse::class.java)
        if (responseObj.success) {
            _isAuthenticated.postValue(true)
        } else {
            val updated = _serverReceivedEvents.value;
            updated?.add(SocketEvent(EventType.ERROR, null))
        }
    }

    inner class SocketBinder : Binder() {
        fun getService(): SocketService {
            return this@SocketService
        }
    }
}