package com.example.uranus.ui.invasions_page

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.R
import com.example.uranus.databinding.ActivityInvasionsBinding
import com.example.uranus.services.SocketService
import com.example.uranus.ui.home_page.data.AuthenticationData
import java.util.*


class InvasionsActivity : AppCompatActivity() {

    private lateinit var invasionsViewModel: InvasionsViewModel
    private lateinit var binding: ActivityInvasionsBinding
    private lateinit var authData: AuthenticationData;
    private var camId: Int = 0;
    private var mService: SocketService = SocketService();
    var mBound = false

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: SocketService.SocketBinder = service as SocketService.SocketBinder
            mService = binder.getService()
            mService.connect(authData)

            mService.serverReceivedEvents.observe(this@InvasionsActivity, Observer {
                val events = it ?: return@Observer
            })

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authData = createAuthData()
        camId = intent.getIntExtra("cam_id", 0)
        val intent1 = Intent(this, SocketService::class.java)
        bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);

        binding = ActivityInvasionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val table = binding.tableLayout

        invasionsViewModel = ViewModelProvider(this, InvasionsViewModelFactory())
            .get(InvasionsViewModel::class.java)

        invasionsViewModel.invasions.observe(this@InvasionsActivity, Observer {
            val invasionsData = it ?: return@Observer
            if (invasionsData.success == true) {
                while (table.getChildCount() > 1) {
                    table.removeView(table.getChildAt(table.getChildCount() - 1))
                }
                invasionsData.invasions?.forEach{ invasion ->
                    run {
                        val displayMetrics = DisplayMetrics()
                        val newRow = TableRow(this)

                        val layout = LinearLayout.LayoutParams(
                            100,
                            100)
                        layout.setMargins(10, 10, 10, 10);
                        newRow.layoutParams = layout

                        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = (invasion.date ?: 0).toLong() * 1000
                        val date = TextView(this);
                        date.text = DateFormat.format("dd-MM-yyyy", cal).toString();
                        date.textSize = 16F
                        date.setTextColor(R.color.purple_200)

                        val intruders = TextView(this);
                        intruders.layoutParams = layout
                        intruders.text = "aaaaaa"
                        intruders.textSize = 16F
                        val shortFragment = Button(this)
                        shortFragment.layoutParams = LinearLayout.LayoutParams(50, 50);
                        shortFragment.setBackgroundResource(R.drawable.video_download_button);
                        shortFragment.setOnClickListener {
                            //TODO show video by link
                        }

                        newRow.addView(date)
                        newRow.addView(intruders)
                        newRow.addView(shortFragment)

                        table.addView(newRow)
                        //val fullFragment =
                        //table.addView()
                    }
                }
            } else {
                // @TODO
            }

        })
        invasionsViewModel.getInvasions(authData, camId)

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
