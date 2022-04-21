package ru.netology.nmedia.ui

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R

const val START_DELAY = 12000L

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val viewRotation = findViewById<StatsView>(R.id.stats_rotation)
        viewRotation.postDelayed({ viewRotation.data = 20F }, 2000)

        val viewSequential = findViewById<StatsView>(R.id.stats_sequential)
        viewSequential.postDelayed({ viewSequential.data = 60F }, 7000)

        val viewBidirectional = findViewById<StatsView>(R.id.stats_bidirectional)
        viewBidirectional.postDelayed({ viewBidirectional.data = 80F }, START_DELAY)

        val viewIconNetology = findViewById<ImageView>(R.id.ic_netology)
        val alphaInvisible = PropertyValuesHolder.ofFloat(View.ALPHA, 1F, 0F)
            ObjectAnimator.ofPropertyValuesHolder(viewIconNetology, alphaInvisible)
                .apply {
                    startDelay = START_DELAY
                    duration = 2000
                    interpolator = LinearInterpolator()
                }.start()
    }
}