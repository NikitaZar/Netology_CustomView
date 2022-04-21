package ru.netology.nmedia.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewRotation = findViewById<StatsView>(R.id.stats_rotation)
        viewRotation.postDelayed({ viewRotation.data = 60F }, 2000)


        val viewSequential = findViewById<StatsView>(R.id.stats_sequential)
        viewSequential.postDelayed({ viewSequential.data = 80F }, 7000)
    }
}