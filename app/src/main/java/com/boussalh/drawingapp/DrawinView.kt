package com.boussalh.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.nio.file.attribute.AttributeView
import java.security.cert.CertPath

class DrawinView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //drawing app
    private lateinit var drawPath: FingerPath

    //defines what to draw
    private lateinit var canvasPaint: Paint

    //defines who to draw
    private lateinit var drawPaint: Paint
    private var color = Color.BLACK
    private lateinit var canvas: Canvas
    private lateinit var canvasBitmap: Bitmap
    private var brushSize: Float = 0.toFloat()
    private var paths = mutableListOf<FingerPath>()

    init {
        setUpDrawing()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap)

    }

    //this function will be called by the system when the user is going to touch the screen
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x //touch event of x coordinate
        val touchY = event?.y //touch event of y coordinate

        when (event?.action) {
            //this event will be fired when the user put finger on the screen
            MotionEvent.ACTION_DOWN -> {
                drawPath.color = color
                drawPath.brushThickness = brushSize.toFloat()

                drawPath.reset() // resetting path before we se initial point
                drawPath.moveTo(touchX!!, touchY!!)
            }
            // the even will be fired when the user starts to move it's finger; this well
            // be fired continually until user pickup the finger
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX!!, touchY!!)
            }
            //this even will be fired when the user will pick up the finger from screen
            MotionEvent.ACTION_UP -> {
                drawPath = FingerPath(color, brushSize)
                paths.add(drawPath)
            }

            else -> return false
        }
        invalidate() // refreshing the layout to reflect the drawing changes
        return true

        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawBitmap(canvasBitmap, 0f, 0f, drawPaint)
        for (path in paths) {
            drawPaint.strokeWidth = path.brushThickness
            drawPaint.color = path.color
            canvas.drawPath(path, drawPaint)//drawing path on canvas
        }
        if (!drawPath.isEmpty) {
            drawPaint.strokeWidth = drawPath.brushThickness
            drawPaint.color = drawPath.color
            canvas.drawPath(drawPath, drawPaint)//drawing path on canvas
        }
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = FingerPath(color, brushSize)
        drawPaint.color = color
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND

        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20.toFloat()
    }

    fun changeBrushSize(newSize: Float) {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics
        )
        drawPaint.strokeWidth = brushSize
    }

    fun setColor(newColor: Any) {
        if (newColor is String){
        color = Color.parseColor(newColor)
        drawPaint.color = color
        }
        else{
            color = newColor as Int
            drawPaint.color = color
        }
    }

    fun undoPath() {
        if (paths.size > 0) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    internal inner class FingerPath(var color: Int, var brushThickness: Float) : Path()
}