package baaahs.plugin.sound_analysis

interface SoundAnalysisPlatform {
    suspend fun listAudioInputs(): List<AudioInput>

    fun createConstantQAnalyzer(
        audioInput: AudioInput,
        sampleRate: Float,

    ): SoundAnalyzer
}

interface AudioInput {
    val id: String
    val title: String
    val defaultSampleRate: Float get() = 44100f
}