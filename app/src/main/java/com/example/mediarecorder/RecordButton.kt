package com.example.mediarecorder

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import com.example.mediarecorder.State.*

class RecordButton(

    context : Context,
    attrs : AttributeSet
) : AppCompatImageButton(context, attrs){

    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }
    fun updateIconWithState(state: State){
        when(state){
          BEFORE_RECORDING ->{
                setImageResource(R.drawable.ic_record)
            }
            ON_RECORDING -> {

                setImageResource(R.drawable.ic_stop24)
            }
            AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop24)
            }
        }
    }
}