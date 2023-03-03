package com.example.vktest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.*
import kotlin.Int
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class WatchView( context: Context, attributesSet: AttributeSet?, defStyleAtr: Int, defStyleRes: Int )
    : View(context, attributesSet, defStyleAtr, defStyleRes) {

    constructor(context: Context, attributesSet: AttributeSet?, defStyleAtr: Int) : this(
        context,
        attributesSet,
        defStyleAtr,
        R.style.DefaultWatch
    )

    constructor(context: Context, attributesSet: AttributeSet?) : this(
        context,
        attributesSet,
        R.attr.WatchStyle
    )

    constructor(context: Context) : this(context, null){
        isSaveEnabled = true
    }

    private val paint = Paint()
    private val rect = Rect()
    private val numbers = arrayListOf(1f, 2f, 3f ,4f, 5f, 6f, 7f,8f ,9f ,10f, 11f, 12f)
    private var radius = 0f
    private val padding = 25
    private var truncation = 0f
    private var fontSize = 0f

    init {
        if (attributesSet != null) {
            initAttributes(attributesSet, defStyleAtr, defStyleAtr)
        }
    }

    private fun initAttributes(attributesSet: AttributeSet?, defStyleAtr: Int, defStyleAtr1: Int) {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        radius = (min(width , height)/2 - padding).toFloat()
        fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 19F, resources.displayMetrics)
        truncation = radius/10
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawPaint(paint)
        canvas.translate(width/2f, height/2f)
        drawCircle(canvas)
        drawNumeral(canvas)
        drawHands(canvas)
        postInvalidateDelayed(400)
        invalidate()
        requestLayout()
    }

    private fun drawNumeral(canvas: Canvas) {
        paint.reset()
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.textSize = fontSize

        for( number in numbers ){
            val tmp = numbers[number.toInt() - 1].toInt().toString()
            paint.getTextBounds(tmp, 0, tmp.length, rect)
            val angel = Math.PI/6f * (number - 3f)
            if(width < height){
                val x = cos(angel) * (radius + 275 )/2f - rect.width()/2f
                val y = sin(angel) * (radius + 275 )/2f + rect.height()/2f
                canvas.drawText(tmp, x.toFloat(), y.toFloat(), paint)
            }else{
                val x = cos(angel) * (radius + 200 )/2f - rect.width()/2f
                val y = sin(angel) * (radius + 200 )/2f + rect.height()/2f
                canvas.drawText(tmp, x.toFloat(), y.toFloat(), paint)
            }
        }
    }

    private fun drawCircle(canvas: Canvas) {
        paint.reset()
        paint.color = Color.BLACK
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        canvas.drawCircle(0f, 0f, radius, paint)
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 30f
        canvas.drawPoint(0f,0f, paint)
    }

    private fun drawHand(canvas: Canvas, loc: Float, isHour: Boolean) {
        val angle = Math.PI * loc/30 - Math.PI/2
        var handRadius = radius - truncation
        if(isHour){
            handRadius -= truncation*2
        }
        canvas.drawLine(0f, 0f, (cos(angle) * handRadius).toFloat(), (sin(angle) * handRadius).toFloat(), paint)
    }

        private fun drawHands(canvas: Canvas) {
            paint.reset()
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            val c = Calendar.getInstance()
            var hour = c.get(Calendar.HOUR_OF_DAY)
            if(hour > 12){ hour -= 12 }
            paint.strokeWidth = 20f
            drawHand(canvas, (hour + c.get(Calendar.MINUTE)/60f ) * 5f, true)
            paint.strokeWidth = 10f
            drawHand(canvas, c.get(Calendar.MINUTE).toFloat(), false)
            paint.strokeWidth = 5f
            drawHand(canvas, c.get(Calendar.SECOND).toFloat(), false)
        }
    companion object{
        private val  TAG = "logs"
    }
    }


