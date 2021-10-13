package baaahs.plugin.sound_analysis

import baaahs.util.Clock
import baaahs.util.Logger
import be.tarsos.dsp.ConstantQ
import java.lang.Thread.sleep
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Mixer
import kotlin.concurrent.thread

class JvmSoundAnalyzer(
    private val clock: Clock
) : SoundAnalyzer {
    private val listeners = hashSetOf<SoundAnalyzer.AnalysisListener>()
    private val inputsListeners = hashSetOf<SoundAnalyzer.InputsListener>()

    private var mixerInfos = getPlaybackMixerInfos()
    private var audioInputs = mixerInfos.map { it.toAudioInput() }

    override var currentAudioInput: AudioInput? = null
    private var currentAnalyzerStream: AnalyzerStream? = null

    // 40hz/12 bins/spread of 2.3 fits into an 8k buffer with 87 buckets; not quite the piano keys but close.
    private val constantQ = ConstantQ(sampleRate, 40f, 6000f, 12f, 0.001f, 2.3f)

    init {
        logger.debug { "FFT length: ${constantQ.ffTlength}; bins: ${constantQ.numberOfOutputBands}" }

        thread(isDaemon = true, name = "JvmSoundAnalyzer Source Watcher") {
            sleep(5000)
            val newMixerInfos = getPlaybackMixerInfos()
            val newAudioInputs = newMixerInfos.map { it.toAudioInput() }
            if (newAudioInputs != audioInputs) {
                mixerInfos = newMixerInfos
                audioInputs = newAudioInputs

                inputsListeners.forEach { it.onChange(newAudioInputs) }
            }
        }
    }

    override fun listAudioInputs(): List<AudioInput> = audioInputs

    override fun switchTo(audioInput: AudioInput?) {
        if (audioInput == currentAudioInput) return
        currentAnalyzerStream?.release()

        if (audioInput == null) return

        val mixerInfo = mixerInfos.find { audioInput.matches(it) }
            ?: return

        val mixer = AudioSystem.getMixer(mixerInfo)

        currentAnalyzerStream = AnalyzerStream(mixer, audioFormat, constantQ, clock) { magnitudes, time ->
            listeners.forEach { it.onSample(SoundAnalyzer.Analysis(constantQ.freqencies, magnitudes, time)) }
        }
    }

    override fun listen(analysisListener: SoundAnalyzer.AnalysisListener): SoundAnalyzer.AnalysisListener {
        listeners.add(analysisListener)
        return analysisListener
    }

    override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
        listeners.remove(analysisListener)
    }

    override fun listen(inputsListener: SoundAnalyzer.InputsListener): SoundAnalyzer.InputsListener {
        inputsListeners.add(inputsListener)
        return inputsListener
    }

    override fun unlisten(inputsListener: SoundAnalyzer.InputsListener) {
        inputsListeners.remove(inputsListener)
    }

    private fun getPlaybackMixerInfos(): List<Mixer.Info> {
        return AudioSystem.getMixerInfo().mapNotNull { mixerInfo ->
            val mixer = AudioSystem.getMixer(mixerInfo)
            logger.warn { "* ${mixerInfo.name}:" }
            mixer.sourceLineInfo.forEach { logger.warn { "** sourceLine: $it"} }
            mixer.targetLineInfo.forEach { logger.warn { "** targetLine: $it"} }
            if (mixer.sourceLineInfo.isNotEmpty()) mixerInfo else null
        }.also {
            logger.warn { "${it.size} playback mixers available:" }
            it.forEach {
                logger.warn { "* ${it.name} (${it.description})" }
            }
        }
    }

    private fun AudioInput.matches(info: Mixer.Info): Boolean {
        return info.toAudioInput() == this
    }

    private fun Mixer.Info.toAudioInput() = AudioInput(name, name)

    companion object {
        private val logger = Logger<JvmSoundAnalyzer>()

        private const val sampleRate = 44100f
        private val audioFormat = AudioFormat(
            sampleRate, 16, /*channels =*/ 1,
            /*signed =*/ true, /*bigEndian =*/ false
        )
    }
}