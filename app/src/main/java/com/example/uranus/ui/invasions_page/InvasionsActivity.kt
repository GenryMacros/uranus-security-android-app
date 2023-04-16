package com.example.uranus.ui.invasions_page

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
                val leftRowMargin = 0
                val topRowMargin = 0
                val rightRowMargin = 0
                val bottomRowMargin = 0
                var textSize = 16F
                var smallTextSize = 12F
                var mediumTextSize = 14F
                var id = 0
                var textSpacer: TextView;
                while (table.getChildCount() > 1) {
                    table.removeView(table.getChildAt(table.getChildCount() - 1))
                }
                invasionsData.invasions?.forEach{ invasion ->
                    run {
                        textSpacer = TextView(this);
                        textSpacer.setText("");
                        val tv = TextView(this);
                        tv.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                                                TableRow.LayoutParams.WRAP_CONTENT)
                        tv.gravity = Gravity.LEFT
                        tv.setPadding(5, 15, 0, 15);

                        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = (invasion.date ?: 0).toLong() * 1000
                        tv.text = DateFormat.format("dd-MM-yyyy", cal).toString();
                        tv.textSize = 16F;

                        val tv2 = TextView(this)
                        tv2.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                                                 TableRow.LayoutParams.MATCH_PARENT)
                        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

                        tv2.gravity = Gravity.LEFT;
                        tv2.setPadding(5, 15, 0, 15);

                        tv2.setTextColor(Color.parseColor("#000000"));
                        tv2.text = "aaaaaaaaaaa";
                        tv2.textSize = 16F;

                        val tv3 = LinearLayout(this)
                        tv3.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            150)

                        tv3.gravity = Gravity.CENTER;
                        tv3.setPadding(5, 15, 0, 15);
                        val tv3b = Button(this);
                        tv3b.layoutParams = TableRow.LayoutParams(120,
                            120)
                        tv3b.setPadding(5, 0, 0, 5);

                        tv3b.setGravity(Gravity.CENTER);
                        tv3b.setBackgroundResource(R.drawable.video_download_button);
                        tv3b.setOnClickListener {
                            //TODO show video by link
                        }
                        tv3.addView(tv3b)

                        val tv4 = LinearLayout(this)
                        tv4.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            150)

                        tv4.gravity = Gravity.CENTER;
                        tv4.setPadding(5, 15, 0, 15);
                        val tv4b = Button(this);
                        tv4b.layoutParams = TableRow.LayoutParams(120,
                            120)
                        tv4b.setPadding(5, 0, 0, 5);

                        tv4b.setGravity(Gravity.CENTER);
                        tv4b.setBackgroundResource(R.drawable.video_download_button);
                        tv4b.setOnClickListener {
                            //TODO show video by link
                        }
                        tv4.addView(tv4b)

                        // add table row
                        val tr = TableRow(this);
                        tr.id = id;
                        id += 1;
                        val trParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                                                150);
                        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                            bottomRowMargin);
                        tr.setPadding(0,0,0,0);
                        tr.layoutParams = trParams;
                        tr.addView(tv);
                        tr.addView(tv2);
                        tr.addView(tv3);
                        tr.addView(tv4);
                        table.addView(tr, trParams);
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
