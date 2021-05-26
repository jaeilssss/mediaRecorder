package com.example.mediarecorder

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context , attributeSet: AttributeSet) :AppCompatTextView(context,attributeSet){

    private var startTimeStamp :Long =  0L

    private val countUpAction :Runnable = object :Runnable{

        override fun run() {

        val currentTimeStamp = SystemClock.elapsedRealtime()

        }

    }

    fun startCountUp(){
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }
    fun stopCountUp(){
        handler?.removeCallbacks(countUpAction)
    }
}