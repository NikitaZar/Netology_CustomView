package ru.netology.nmedia.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

const val TYPE_ROTATION = 0
const val TYPE_SEQUENTIAL = 1
const val TYPE_BIDIRECTIONAL = 2

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()
    private var animationType = 0
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            colors = listOf(
                getColor(
                    R.styleable.StatsView_color1,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color2,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color3,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color4,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color5,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color6,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color7,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color8,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color9,
                    randomColor()
                ),
                getColor(
                    R.styleable.StatsView_color10,
                    randomColor()
                )
            )

            animationType = getInteger(R.styleable.StatsView_animationType, 0)
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: Float = 0F
        set(value) {
            field = value
            update()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {

        if (data !in 0F..100F) {
            return
        }

        when (animationType) {
            TYPE_ROTATION -> {
                rotate(canvas)
            }
            TYPE_SEQUENTIAL -> {
                sequent(canvas)
            }
            TYPE_BIDIRECTIONAL -> {
                bidirectional(canvas)
            }
        }
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }

    private fun rotate(canvas: Canvas) {

        paint.color = getColor(context, R.color.back_color)
        canvas.drawCircle(center.x, center.y, radius, paint)

        val dataL = data
        val dataPercent = listOf(0F, 0F, 0F, 0F).map { dataL / 100F }

        var startFrom = -90F
        var zeroStartFrom = startFrom + 1F
        var zeroPaintColor = 0
        for ((index, datum) in dataPercent.withIndex()) {
            val angle = 90F * datum
            paint.color = colors.getOrNull(index) ?: randomColor()
            if (index == 0 && progress == 1F) {
                zeroPaintColor = paint.color
                zeroStartFrom = startFrom
            }
            canvas.drawArc(
                oval,
                startFrom + (progress * 360F),
                angle * progress,
                false,
                paint
            )

            if (progress == 1F) {
                paint.color = zeroPaintColor
                canvas.drawArc(oval, zeroStartFrom, 1F, false, paint)
            }

            startFrom += 90F
        }

        canvas.drawText(
            "%.2f%%".format(data * progress),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun sequent(canvas: Canvas) {
        paint.color = getColor(context, R.color.back_color)
        canvas.drawCircle(center.x, center.y, radius, paint)

        var dataL = data
        val dataPercent = listOf(25F, 25F, 25F, 25F).map {
            if (dataL >= it) {
                dataL -= it
                it / 100F
            } else {
                val res = dataL / 100F
                dataL = 0F
                res
            }
        }

        var startFrom = -90F
        var zeroStartFrom = startFrom + 1F
        var zeroPaintColor = 0
        var datumSum = 0F
        val dataInProgress = data * progress
        val dataInProgressL = dataInProgress / 100F
        for ((index, datum) in dataPercent.withIndex()) {
            val angle = 360F * datum
            paint.color = colors.getOrNull(index) ?: randomColor()

            if (index == 0 && progress == 1F) {
                zeroPaintColor = paint.color
                zeroStartFrom = startFrom
            }

            val progressSeg = if (dataInProgressL <= datumSum + datum) {
                dataInProgressL - datumSum
            } else {
                datum
            } * dataPercent.size
            datumSum += datum
            val sweepAngel = angle * progressSeg

            canvas.drawArc(
                oval,
                startFrom,
                sweepAngel,
                false,
                paint
            )

            startFrom += angle

            if (dataInProgressL < datumSum) {
                break
            }
        }

        if (progress == 1F) {
            paint.color = zeroPaintColor
            canvas.drawArc(oval, zeroStartFrom, 1F, false, paint)
        }
        canvas.drawText(
            "%.2f%%".format(dataInProgress),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun bidirectional(canvas: Canvas) {

        paint.color = getColor(context, R.color.back_color)
        canvas.drawCircle(center.x, center.y, radius, paint)

        val dataL = data
        val dataPercent = listOf(0F, 0F, 0F, 0F).map { dataL / 100F }

        var startFrom = -45F
        var zeroStartFrom = startFrom + 1F
        var zeroPaintColor = 0

        for ((index, datum) in dataPercent.withIndex()) {
            val angle = 45F * datum
            paint.color = colors.getOrNull(index) ?: randomColor()
            if (index == 0 && progress == 1F) {
                zeroPaintColor = paint.color
                zeroStartFrom = startFrom
            }
            canvas.drawArc(
                oval,
                startFrom,
                angle * progress,
                false,
                paint
            )

            canvas.drawArc(
                oval,
                startFrom,
                angle * progress * -1F,
                false,
                paint
            )
            if (progress == 1F) {
                paint.color = zeroPaintColor
                canvas.drawArc(oval, zeroStartFrom, 1F, false, paint)
            }

            startFrom += 90F
        }

        canvas.drawText(
            "%.2f%%".format(data * progress),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }
}