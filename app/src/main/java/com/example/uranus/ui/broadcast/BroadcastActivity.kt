package com.example.uranus.ui.broadcast

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.util.Base64
import android.widget.Button
import androidx.lifecycle.Observer
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.uranus.R
import com.example.uranus.databinding.ActivityBroadcastBinding
import com.example.uranus.services.EventType
import com.example.uranus.services.SocketEvent
import com.example.uranus.services.SocketService
import com.example.uranus.ui.invasions_page.data.FramesPayload
import com.google.gson.Gson


class BroadcastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBroadcastBinding
    private lateinit var view: ImageView
    private lateinit var backButton: Button
    private var gson = Gson();
    private var mService: SocketService = SocketService()
    var mBound = false
    var count = 0

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: SocketService.SocketBinder = service as SocketService.SocketBinder
            mService = binder.getService()
            mService.serverReceivedEvents.observe(this@BroadcastActivity, Observer {
                val events = it ?: return@Observer
                events.forEach { event ->
                    run {
                        when (event.type) {
                            EventType.FRAMES -> {
                                val responseObj: FramesPayload = gson.fromJson(event.body.toString(),
                                       FramesPayload::class.java)
                                val byteArr = Base64.decode(responseObj.frames[0].buffer, Base64.DEFAULT)
                                val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArr,
                                    0,
                                    byteArr.size)
                                view.setImageBitmap(bmp)
                            }
                            else -> {}
                        }
                    }
                }
                events.clear()
                mService.sendEvent(SocketEvent(EventType.READ_FRAMES, null))
                count++
            })
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent1 = Intent(this, SocketService::class.java)
        bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);

        binding = ActivityBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        view = binding.view
        backButton = findViewById(R.id.back)

        backButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, BroadcastActivity::class.java)
            context.startActivity(intent)
        }
    }
}
