package baaahs

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.ConstantQ
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.sound.sampled.*

class JvmSoundAnalyzer : SoundAnalyzer {
    override val frequencies: FloatArray
        get() = cqtAnalyzer.frequencies

    private val sampleRate = 44100f
    private var cqtAnalyzer: CqtAnalyzer
    private val listeners = mutableListOf<SoundAnalyzer.AnalysisListener>()

    init {
        cqtAnalyzer = createConstantQAnalyzer(getAudioInput())
    }

    override fun listen(analysisListener: SoundAnalyzer.AnalysisListener) {
        listeners.add(analysisListener)
    }

    override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
        listeners.remove(analysisListener)
    }

    companion object {
        private val logger = Logger("JvmMediaDevices")
    }

    private fun createConstantQAnalyzer(mixer: Mixer): CqtAnalyzer {
        logger.info { "Analyzing sound from ${mixer.mixerInfo.name}" }
        return CqtAnalyzer(mixer)
    }

    inner class CqtAnalyzer(mixer: Mixer) {
        val frequencies: FloatArray

        private val dispatcher: AudioDispatcher
        private val thread: Thread

        init {
            val format = AudioFormat(
                sampleRate, 16, /*channels =*/ 1,
                /*signed =*/ true, /*bigEndian =*/ false
            )

            // 91hz/12 bits so it fits into an 8k buffer
            val constantQ = ConstantQ(sampleRate, 91f, 20000f, 12f)
            logger.debug { "FFT length: ${constantQ.ffTlength}" }
            val dataLineInfo = DataLine.Info(TargetDataLine::class.java, format)
            val line: TargetDataLine
            line = mixer.getLine(dataLineInfo) as TargetDataLine
            val bufferSize = constantQ.ffTlength
            val numberOfSamples = bufferSize
            line.open(format, numberOfSamples)
            line.start()
            val stream = AudioInputStream(line)

            val audioStream = JVMAudioInputStream(stream)
            // create a new dispatcher
            val bufferOverlap = bufferSize / 2
            dispatcher = AudioDispatcher(audioStream, bufferSize, bufferOverlap)

            frequencies = constantQ.freqencies.copyOf()
            dispatcher.addAudioProcessor(object : AudioProcessor {
                override fun process(audioEvent: AudioEvent?): Boolean {
                    constantQ.process(audioEvent)
                    val analysis = SoundAnalyzer.Analysis(frequencies, constantQ.magnitudes.copyOf())

                    GlobalScope.launch {
                        listeners.forEach { it.onSample(analysis) }
                    }
                    return true
                }

                override fun processingFinished() {
                }
            })
            thread = Thread(dispatcher, "JvmMediaDevices Audio Processor")
            thread.start()
        }

        fun finalize() {
            close()
        }

        fun close() {
            dispatcher.stop()
            thread.join()
        }
    }

    private fun getAudioInput(): Mixer {
        val playbackMixerInfos = getPlaybackMixerInfos()
        logger.debug { "${playbackMixerInfos.size} playback mixers available:" }
        playbackMixerInfos.forEach { logger.debug { "* ${it.name}" } }
        val mixer = AudioSystem.getMixer(playbackMixerInfos.find { it.name == "Default Audio Device" })
        return mixer!!
    }

    fun getPlaybackMixerInfos(): List<Mixer.Info> {
        return AudioSystem.getMixerInfo().mapNotNull { info ->
            val mixer = AudioSystem.getMixer(info)
            // Mixer capable of audio play back if source LineWavelet length != 0
            if (mixer.sourceLineInfo.isNotEmpty()) info else null
        }

    }
}