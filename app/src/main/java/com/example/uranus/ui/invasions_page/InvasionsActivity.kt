package com.example.uranus.ui.invasions_page

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.downloader.*
import com.example.uranus.R
import com.example.uranus.databinding.ActivityInvasionsBinding
import com.example.uranus.services.EventType
import com.example.uranus.services.SocketService
import com.example.uranus.services.data.ReauthData
import com.example.uranus.ui.broadcast.BroadcastActivity
import com.example.uranus.ui.home_page.data.AuthenticationData
import com.google.gson.Gson
import java.text.SimpleDateFormat


class InvasionsActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 1
    private lateinit var invasionsViewModel: InvasionsViewModel
    private lateinit var binding: ActivityInvasionsBinding
    private lateinit var authData: AuthenticationData
    private lateinit var videoView: VideoView
    private lateinit var refreshButton: Button
    private lateinit var broadcastButton: Button
    private var camId: Int = 0
    private var mService: SocketService = SocketService()
    private var gson = Gson()
    var mBound = false

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: SocketService.SocketBinder = service as SocketService.SocketBinder
            mService = binder.getService()
            mService.connect(authData)

            mService.serverReceivedEvents.observe(this@InvasionsActivity, Observer {
                val events = it ?: return@Observer
                events.forEach{
                        event -> run {
                            when (event.type) {
                                EventType.REAUTH_HAPPENED -> {
                                    val reauthObj: ReauthData = gson.fromJson(event.body.toString(),
                                        ReauthData::class.java)
                                    authData.token = reauthObj.new_token
                                }
                                else -> {}
                            }
                        }
                }
                events.clear()
            })

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    @SuppressLint("ResourceAsColor", "SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
        }
        authData = createAuthData()
        camId = intent.getIntExtra("cam_id", 0)
        if (!mBound) {
            val intent1 = Intent(this, SocketService::class.java)
            bindService(intent1, mConnection, BIND_AUTO_CREATE)
        }

        binding = ActivityInvasionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshButton = findViewById(R.id.refresh)
        broadcastButton = findViewById(R.id.live)
        videoView = findViewById(R.id.videoView)
        val table = binding.tableLayout

        refreshButton.setOnClickListener {
            invasionsViewModel.getInvasions(authData, camId)
        }

        broadcastButton.setOnClickListener {
            BroadcastActivity.startActivity(this, camId)
        }

        invasionsViewModel = ViewModelProvider(this, InvasionsViewModelFactory())[InvasionsViewModel::class.java]

        invasionsViewModel.token.observe(this@InvasionsActivity, Observer {
            val token = it ?: return@Observer
            authData.token = token
            invasionsViewModel.getInvasions(authData, camId)
        })

        invasionsViewModel.is_updated.observe(this@InvasionsActivity, Observer {
            val invasionsData = invasionsViewModel.invasions.value
            if (invasionsData != null) {
                if (invasionsData.success == true) {
                    val buttonWidth = 120
                    val buttonHeight = 120
                    val tableRowOffset = 20
                    val bottomRowMargin = 0
                    val textSize = 16F
                    var id = 0
                    var textSpacer: TextView

                    while (table.childCount > 1) {
                        table.removeView(table.getChildAt(table.childCount - 1))
                    }
                    println("invasions")
                    invasionsData.invasions?.forEach{ invasion ->
                        run {

                            textSpacer = TextView(this)
                            textSpacer.text = ""
                            val dateView = TextView(this)
                            dateView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.WRAP_CONTENT)
                            dateView.gravity = Gravity.CENTER
                            dateView.setPadding(5, 40, 0, 0)

                            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                            dateView.setTextColor(Color.parseColor("#df57d3"))
                            dateView.text = dateFormat.format( (invasion.date ?: 0).toLong() * 1000L)
                            dateView.textSize = 15F

                            val intrudersView = TextView(this)
                            intrudersView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.MATCH_PARENT)
                            intrudersView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

                            intrudersView.gravity = Gravity.CENTER
                            intrudersView.setPadding(5, 0, 0, 5)

                            intrudersView.setTextColor(Color.parseColor("#000000"))
                            var invaders = ""
                            for (intruder in invasion.invaders!!) {
                                invaders += when (intruder) {
                                    "cat" -> {
                                        "<font color=" + "#aca98a" + ">cat </font>"
                                    }
                                    "person" -> {
                                        "<font color=" + "#ed7370" + ">person </font>"
                                    }
                                    else -> {
                                        "<font color=#ababab>$intruder</font>\n"
                                    }
                                }

                            }
                            intrudersView.text = Html.fromHtml(invaders)
                            intrudersView.textSize = 16F

                            val videoButtonHolder = LinearLayout(this)
                            videoButtonHolder.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                150)

                            videoButtonHolder.gravity = Gravity.CENTER
                            videoButtonHolder.setPadding(0, 15, 0, 30)
                            val videoButton = Button(this)
                            videoButton.layoutParams = TableRow.LayoutParams(buttonWidth, buttonHeight)
                            videoButton.setPadding(0, 0, 0, 0)

                            videoButton.gravity = Gravity.CENTER
                            videoButton.setBackgroundResource(R.drawable.video_download_button)
                            videoButton.setOnClickListener {
                                downloadVideoByUrl(invasion.file_name, invasion.link)
                            }
                            videoButtonHolder.addView(videoButton)

                            val tr = TableRow(this)
                            tr.id = id
                            id += 1
                            val trParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                buttonHeight + tableRowOffset)
                            trParams.setMargins(0, 0, 0,
                                bottomRowMargin)
                            tr.setPadding(0,20,0,0)
                            tr.layoutParams = trParams
                            tr.setBackgroundColor(if (id % 2 == 0) Color.parseColor("#414244") else Color.parseColor("#37383a"))
                            tr.addView(dateView)
                            tr.addView(intrudersView)
                            tr.addView(videoButtonHolder)
                            table.addView(tr, trParams)
                        }
                    }
                } else {
                    // @TODO
                }
            }

        })
        invasionsViewModel.getInvasions(authData, camId)

    }

    private fun downloadVideoByUrl(fileName: String?, url: String?) {
        val context = this
        PRDownloader.download(url, Environment.getExternalStorageDirectory().absolutePath, fileName)
            .setHeader("Accept-Encoding", "identity")
            .build()
            .setOnStartOrResumeListener { }
            .setOnPauseListener { }
            .setOnCancelListener { }
            .setOnProgressListener { }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    showVideo(context, Environment.getExternalStorageDirectory().absolutePath + '/' + fileName)
                }
                override fun onError(error: Error?) {}
            })
    }

    @SuppressLint("InflateParams")
    private fun showVideo(context: Context, path: String?) {
        val inflater =
            ContextCompat.getSystemService(context, LayoutInflater::class.java)

        val pw = PopupWindow(inflater?.inflate(R.layout.video_popup,
            null, false), 1080, 1920, true)
        pw.isFocusable = true
        val pwVideoView = pw.contentView.findViewById<VideoView>(R.id.videoView)
        val dismissButton1 = pw.contentView.findViewById<Button>(R.id.dismiss1)
        val dismissButton2 = pw.contentView.findViewById<Button>(R.id.dismiss2)
        dismissButton1.setOnClickListener {
            pw.dismiss()
        }
        dismissButton2.setOnClickListener {
            pw.dismiss()
        }
        val mediaController = MediaController(context)
        mediaController.setAnchorView(pwVideoView)
        pwVideoView.setVideoPath(path)
        pwVideoView.setMediaController(mediaController)
        pwVideoView.requestFocus()
        pwVideoView.start()
        pw.showAtLocation(pwVideoView, Gravity.CENTER, 0, -100)
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

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            AlertDialog.Builder(this)
                .setTitle("To Write to DB Allow Permissions")
                .setMessage("On the Next Screen to Remove the App\n\nCheck Don't Ask Again and Click DENY\n\nOr Click ALLOW to Grant Permission")
                .setPositiveButton("NEXT") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    companion object {
        fun startActivity(context: Context, login_data: AuthenticationData, cam_id: Int) {
            val intent = Intent(context, InvasionsActivity::class.java)
            intent.putExtra("public_key", login_data.publicKey)
            intent.putExtra("auth_token", login_data.token)
            intent.putExtra("refresh_token", login_data.refreshToken)
            intent.putExtra("user_id", login_data.userId)
            intent.putExtra("cam_id", cam_id)
            context.startActivity(intent)
        }
    }
}

