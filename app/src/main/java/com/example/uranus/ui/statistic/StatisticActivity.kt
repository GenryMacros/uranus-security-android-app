package com.example.uranus.ui.statistic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uranus.R
import androidx.lifecycle.Observer
import com.example.uranus.databinding.ActivityStatisticBinding
import com.example.uranus.ui.home_page.HomeAuthData
import com.example.uranus.ui.home_page.data.AuthenticationData
import java.text.SimpleDateFormat

class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticBinding
    private lateinit var backButton: Button
    private lateinit var invasionsText: TextView
    private lateinit var intrudersText: TextView
    private lateinit var lastInvasionText: TextView
    private lateinit var durationText: TextView
    private lateinit var statisticsViewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        invasionsText = findViewById(R.id.invasions)
        intrudersText = findViewById(R.id.intruders)
        lastInvasionText = findViewById(R.id.last_invasion)
        durationText = findViewById(R.id.sessions_duration)
        backButton = findViewById(R.id.back)

        statisticsViewModel = ViewModelProvider(this, StatisticViewModelFactory())
            .get(StatisticsViewModel::class.java)

        statisticsViewModel.is_updated.observe(this@StatisticActivity, Observer {
            val invasionsData = statisticsViewModel.statisticsData.value
            if (invasionsData != null) {
                invasionsText.text = invasionsData.invasions.toString()
            }
            if (invasionsData != null) {
                intrudersText.text = invasionsData.intruders.toString()
            }

            if (invasionsData != null) {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
                lastInvasionText.text = dateFormat.format( (invasionsData.latest ?: 0).toLong() * 1000L)
            }
            if (invasionsData != null) {
                durationText.text = invasionsData.duration.toString()
            }
        })

        backButton.setOnClickListener {
            finish()
        }

        statisticsViewModel.getStatistic(createAuthData(), 1)
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
        fun startActivity(context: Context, login_data: AuthenticationData) {
            val intent = Intent(context, StatisticActivity::class.java)
            intent.putExtra("public_key", login_data.publicKey)
            intent.putExtra("auth_token", login_data.token)
            intent.putExtra("refresh_token", login_data.refreshToken)
            intent.putExtra("user_id", login_data.userId)
            context.startActivity(intent)
        }
    }
}
