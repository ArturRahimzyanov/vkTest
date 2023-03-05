package com.example.vktest

import android.content.Context
import android.graphics.*
import android.graphics.BitmapFactory.decodeResource
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import java.util.*
import kotlin.Int
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.properties.Delegates


class WatchView( context: Context, attributesSet: AttributeSet?, defStyleAtr: Int, defStyleRes: Int )
    : androidx.appcompat.widget.AppCompatImageView(context, attributesSet, defStyleAtr) {

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
    private var bitmap: Bitmap by Delegates.notNull()
    private var radius = 0
    private val padding = 25
    private var handTruncation = 0
    private var hourHandTruncation = 0
    private  var hours = 0
    private  var minutes = 0
    private  var seconds = 0

    init {
        if (attributesSet != null) {
            initAttributes(attributesSet, defStyleAtr, defStyleAtr)
        }
        isSaveEnabled = true

        bitmap = decodeResource(resources, R.drawable.watcgpng)

        scaleType = ScaleType.CENTER
        Log.d(TAG, "$width , $height, ${bitmap.width}" )
    }

    private fun initAttributes(attributesSet: AttributeSet?, defStyleAtr: Int, defStyleAtr1: Int) {
        val typedArray = context.obtainStyledAttributes(attributesSet, R.styleable.WatchView, defStyleAtr, defStyleAtr1 )
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val min: Int = min(height, width)
        radius = (min / 2 - padding)
        handTruncation = (min / 20)
        hourHandTruncation = (min / 7)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY)
        drawWatches(canvas)
        drawCenter(canvas)
        drawHands(canvas)
        postInvalidateDelayed(400)
        invalidate()
        requestLayout()
    }

    private fun drawHand(canvas: Canvas, loc: Double, isHour: Boolean, isMinute: Boolean) {
        val angle = Math.PI * loc / 30 - Math.PI / 2
        var handRadius = 0
        handRadius = when {
            isHour -> {
                radius - handTruncation - hourHandTruncation
            }
            isMinute -> {
                radius - handTruncation
            }
            else -> {
                radius - handTruncation
            }
        }
        canvas.drawLine(
            (width / 2).toFloat(), (height / 2).toFloat(),
            (width / 2 + cos(angle) * handRadius).toFloat(),
            (height / 2 + sin(angle) * handRadius).toFloat(),
            paint
        )
    }

    private fun drawHands(canvas: Canvas) {
        val c = Calendar.getInstance()
        hours = c.get(Calendar.HOUR_OF_DAY)
        hours = if (hours > 12) {
            hours - 12
        } else {
            hours
        }
        minutes = c.get(Calendar.MINUTE)
        seconds = c.get(Calendar.SECOND)
        drawHand(
            canvas, ((hours + minutes / 60) * 5F).toDouble(),
            isHour = true,
            isMinute = false
        )
        drawHand(canvas, minutes.toDouble(), isHour = false, isMinute = true)
        drawHand(canvas, seconds.toDouble(), isHour = false, isMinute = false)
    }

    private fun drawCenter(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), 12F, paint)
    }

    private fun drawWatches(canvas: Canvas) {
        paint.reset()
        paint.color = Color.BLACK
        paint.strokeWidth = 10F
        paint.style = Paint.Style.STROKE;
        val bitMap = decodeResource(resources, R.drawable.watcgpng)
        val resizedBitmap = Bitmap.createScaledBitmap(bitMap, width, height, true)
        canvas.drawBitmap(
            resizedBitmap,
            ((width - resizedBitmap.width) / 2).toFloat(),
            ((height - resizedBitmap.height) / 2).toFloat(),
            paint
        )
    }

    override fun onSaveInstanceState() : Parcelable? {
        Log.d(TAG, "onSaveSate $id")
       val savedState = SavedState(super.onSaveInstanceState())
        savedState.hours = hours
        savedState.minutes = minutes
        savedState.seconds = seconds
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Log.d(TAG, "onRestoreSate + $id")
        super.onRestoreInstanceState(state)
        if(state is SavedState){
            super.onRestoreInstanceState(state)
            hours = state.hours
            minutes = state.minutes
            seconds = state.seconds
        }else{
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState, Parcelable {

        var hours = 0
        var minutes = 0
        var seconds = 0

        constructor( superState: Parcelable? ) : super(superState)

        constructor(src: Parcel) : super(src) {
            hours = src.readInt()
            minutes = src.readInt()
            seconds = src.readInt()
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            super.writeToParcel(dst, flags)
            dst.writeInt(hours)
            dst.writeInt(minutes)
            dst.writeInt(seconds)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    companion object{
        private val  TAG = "logs"
    }
    }


