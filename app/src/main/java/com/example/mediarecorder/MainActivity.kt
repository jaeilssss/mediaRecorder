package com.example.mediarecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private val countUpView : CountUpView by lazy {
        findViewById<CountUpView>(R.id.recordTextView)
    }
    private val soundVisualizerView : SoundVisualizerView by lazy {
        findViewById<SoundVisualizerView>(R.id.SoundVisualizerView)
    }
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
        resetButton.isEnabled =(value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
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
        initVariables()
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
    soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?:0

    }
    resetButton.setOnClickListener {
        stopPlaying()
        state = State.BEFORE_RECORDING

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
    private fun initVariables(){
        state  = State.BEFORE_RECORDING
    }
    private fun startRecording(){
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            countUpView.startCountUp()
            prepare()
        }
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        state = State.ON_RECORDING
    }

    private fun playing(){
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.start()
        state = State.ON_PLAYING
        countUpView.startCountUp()
        soundVisualizerView.startVisualizing(true)

    }

    private fun stopPlaying(){
        player?.release()
        player = null
        state = State.AFTER_RECORDING
        countUpView.stopCountUp()
        soundVisualizerView.stopVisualizing()
    }
    private fun stopRecording(){
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        countUpView.stopCountUp()
        soundVisualizerView.stopVisualizing()
        state = State.AFTER_RECORDING
    }
    companion object{
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}