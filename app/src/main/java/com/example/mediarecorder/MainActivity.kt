package com.example.mediarecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val recordButton : RecordButton by lazy {
        findViewById<RecordButton>(R.id.recordButton)
    }
    private var state : State = State.BEFORE_RECORDING
    set(value) {
        field = value
        recordButton.updateIconWithState(value)
    }

    private val resetButton : Button by lazy {
        findViewById<Button>(R.id.resetButton)
    }
    private var recorder : MediaRecorder ?= null

    private var player : MediaPlayer ? = null

    private val recordingFilePath : String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        initViews()
        bindViews()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted = requestCode== REQUEST_RECORD_AUDIO_PERMISSION
                && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if(!audioRecordPermissionGranted){
            finish()
        }
    }

    private fun requestPermission(){
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }
  private fun initViews(){
        recordButton.updateIconWithState(state)
    }
private fun bindViews(){
    resetButton.setOnClickListener {
        
    }
    recordButton.setOnClickListener {
        when(state){
            State.BEFORE_RECORDING-> startRecording()
            State.ON_RECORDING -> stopRecording()
            State.AFTER_RECORDING -> playing()
            State.ON_PLAYING ->stopPlaying()
        }
    }
}
    private fun startRecording(){
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        state = State.ON_RECORDING
    }

    private fun playing(){
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.start()
        state = State.ON_PLAYING

    }

    private fun stopPlaying(){
        player?.release()
        player = null
        state = State.AFTER_RECORDING
    }
    private fun stopRecording(){
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        state = State.AFTER_RECORDING
    }
    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}