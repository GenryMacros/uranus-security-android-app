package com.example.uranus.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.example.uranus.ui.home_page.data.AuthenticationResponse
import com.example.uranus.ui.home_page.data.InvasionPayload
import com.example.uranus.ui.invasions_page.utility.Notificator
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class SocketService : Service() {
    private var gson = Gson()
    private val mBinder: IBinder = SocketBinder()
    private val _serverReceivedEvents = MutableLiveData<MutableList<SocketEvent>?>()
    var serverReceivedEvents: LiveData<MutableList<SocketEvent>?> = _serverReceivedEvents
    private val _isAuthenticated = MutableLiveData<Boolean>()
    var isAuthenticated: LiveData<Boolean> = _isAuthenticated
    private var notificator: Notificator = Notificator(this)

    private lateinit var mSocket: Socket
    private lateinit var authData: AuthenticationData

    @Override
    override fun onCreate() {
    }

    fun connect(auth_data: AuthenticationData) {
        authData = auth_data
        if (!mSocket.connected()) {
            _serverReceivedEvents.postValue(mutableListOf())
            _isAuthenticated.postValue(false)
            mSocket.on(EventType.INVASION.toString().lowercase(), onInvasion)
            mSocket.on(EventType.ASK_AUTHENTICATE.toString().lowercase(), onAuthAsk)
            mSocket.on(EventType.FRAMES.toString().lowercase(), onFrames)
            mSocket.on(EventType.REAUTH_HAPPENED.toString().lowercase(), onReauth)
            mSocket.connect()
            notificator.init()
        }
    }


    override fun onBind(intent: Intent): IBinder {
        try {
            mSocket = IO.socket("http://192.168.0.107:8086")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        _isAuthenticated.postValue(_isAuthenticated.value)
        return mBinder
    }

    private var onFrames = Emitter.Listener { data -> kotlin.run {
            val updated = _serverReceivedEvents.value
            updated?.add(SocketEvent(EventType.FRAMES, data[0] as JSONObject?))
            _serverReceivedEvents.postValue(updated)
        }
    }

    private var onInvasion = Emitter.Listener { data -> kotlin.run {
            val responseObj: InvasionPayload = gson.fromJson(data[0].toString(),
                InvasionPayload::class.java)
            notificator.sendNotification(responseObj)
        }
    }

    private var onReauth = Emitter.Listener { data -> kotlin.run {
            val updated = _serverReceivedEvents.value
            updated?.add(SocketEvent(EventType.FRAMES, data[0] as JSONObject?))
        }
    }

    private var onAuthAsk = Emitter.Listener {
        mSocket.emit(EventType.AUTHENTICATE.toString().lowercase(), JSONObject(gson.toJson(authData)),
            Ack { args -> authenticateCallback(args) })
    }

    fun removeObservedEvent(event: SocketEvent) {
        _serverReceivedEvents.value?.remove(event)
    }

    fun sendEvent(event: SocketEvent) {
        when(event.type) {
            EventType.GET_CAMERAS -> {
                mSocket.emit(EventType.GET_CAMERAS.toString().lowercase(),
                    Ack { args -> getCamerasCallback(args) })
            }
            EventType.READ_FRAMES -> {
                mSocket.emit(EventType.READ_FRAMES.toString().lowercase())
            }
            EventType.AUTHENTICATE -> {
                mSocket.emit(EventType.AUTHENTICATE.toString().lowercase(), event.body,
                    Ack { args -> authenticateCallback(args) })
            }
            else -> {}
        }
    }

    private fun getCamerasCallback(vararg args: Any) {
        val response: JSONObject = (args[0] as Array<*>)[0] as JSONObject
        val updated = _serverReceivedEvents.value
        updated?.add(SocketEvent(EventType.GET_CAMERAS, response))
        _serverReceivedEvents.postValue(updated)
    }

    private fun authenticateCallback(vararg args: Any) {
        val response: JSONObject = (args[0] as Array<*>)[0] as JSONObject
        val responseObj: AuthenticationResponse = gson.fromJson(response.toString(),
            AuthenticationResponse::class.java)
        if (responseObj.success) {
            _isAuthenticated.postValue(true)
        } else {
            val updated = _serverReceivedEvents.value
            updated?.add(SocketEvent(EventType.ERROR, null))
        }
    }

    inner class SocketBinder : Binder() {
        fun getService(): SocketService {
            return this@SocketService
        }
    }
}