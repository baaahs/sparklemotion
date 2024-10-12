package baaahs.plugin.sound_analysis

import baaahs.util.Time

interface SoundAnalyzer {
    val currentAudioInput: AudioInput?

    fun listAudioInputs(): List<AudioInput>
    suspend fun switchTo(audioInput: AudioInput?)

    fun listen(analysisListener: AnalysisListener): AnalysisListener
    fun unlisten(analysisListener: AnalysisListener)

    fun listen(inputsListener: InputsListener): InputsListener
    fun unlisten(inputsListener: InputsListener)

    fun interface AnalysisListener {
        fun onSample(analysis: Analysis)
    }

    fun interface InputsListener {
        fun onChange(audioInputs: List<AudioInput>, currentInput: AudioInput?)
    }

    class Analysis(val frequencies: FloatArray, val magnitudes: FloatArray, val timestamp: Time)
}