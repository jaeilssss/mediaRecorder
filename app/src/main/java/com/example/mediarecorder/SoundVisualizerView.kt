package com.example.mediarecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.random.Random

class SoundVisualizerView(
        context : Context,
        attributeSet: AttributeSet ?=null
) : View(context,attributeSet){

    var onRequestCurrentAmplitude : (() -> Int) ? = null

    private var isReplaying : Boolean = false

    private var replayingPosition : Int = 0;

  private  val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }
            //ANTI_ALIAS_FLAG 각저서 그려지는 것을 방지하는 프레그먼트 곡선을 부드럽게 표현 함

   private var drawingWidth : Int  = 0

    private var drawingHeight : Int = 0;

    private var drawingAmplitudes  : List<Int> = emptyList()



    private val visualizeRepeatAction : Runnable = object : Runnable {
        override fun run() {
            // Amplitude, Draw
        if(!isReplaying) {


            val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
            drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
/*

             위에 invoke() 메소드가 실행 하면 메인 엑티비티에서
            soundVisualizerView.onRequestCurrentAmplitude = {
                recorder?.maxAmplitude ?:0

            }
            이 부분이 실행되는것 */
        }else{
            replayingPosition++;
        }
            invalidate()




            handler?.postDelayed(this, ACTION_INTERVAL)

        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2f

        var offsetX = drawingWidth.toFloat()

    drawingAmplitudes
            .let {amplitudes->
                if(isReplaying){
                    amplitudes.takeLast(replayingPosition)
                }else{
                    amplitudes
                }
            }
            .forEach { amplitude->
        val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

        offsetX -= LINE_SPACE

        if(offsetX <0) return@forEach

        canvas.drawLine(
                offsetX,
                centerY - lineLength /2f,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
        )
    }
    }

    fun startVisualizing(isReplaying : Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)

    }
    fun stopVisualizing(){
        handler?.removeCallbacks(visualizeRepeatAction)
    }
    companion object{
        private const val LINE_WIDTH = 10F

        private const val LINE_SPACE = 15F

        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()

        private const val ACTION_INTERVAL = 20L
    }
}