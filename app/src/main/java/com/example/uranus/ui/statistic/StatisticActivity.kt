package com.example.uranus.ui.statistic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.uranus.R
import com.example.uranus.databinding.ActivityStatisticBinding

class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticBinding
    private lateinit var backButton: Button
    private lateinit var invasionsText: TextView
    private lateinit var intrudersText: TextView
    private lateinit var lastInvasionText: TextView
    private lateinit var durationText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        invasionsText = findViewById(R.id.invasions)
        intrudersText = findViewById(R.id.intruders)
        lastInvasionText = findViewById(R.id.last_invasion)
        durationText = findViewById(R.id.sessions_duration)
        backButton = findViewById(R.id.back)

        invasionsText.text = "10"
        intrudersText.text = "10"
        lastInvasionText.text = "24.04.2023"
        durationText.text = "10"
        backButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, StatisticActivity::class.java)
            context.startActivity(intent)
        }
    }
}
