package baaahs.plugin.sound_analysis

import baaahs.util.Logger
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.ConstantQ
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import javax.sound.sampled.*
import kotlin.concurrent.thread

class JvmSoundAnalyzer(
    mixer: Mixer,
    private val sampleRate: Float
) : SoundAnalyzer {
    override val numberOfBuckets: Int
        get() = cqtAnalyzer.numberOfBuckets

    private var cqtAnalyzer: CqtAnalyzer = createConstantQAnalyzer(mixer)
    private val listeners = mutableListOf<SoundAnalyzer.AnalysisListener>()

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
        val numberOfBuckets: Int
        val frequencies: FloatArray

        private val dispatcher: AudioDispatcher
        private val thread: Thread

        init {
            val format = AudioFormat(
                sampleRate, 16, /*channels =*/ 1,
                /*signed =*/ true, /*bigEndian =*/ false
            )

            // 40hz/12 bins/spread of 2.3 fits into an 8k buffer with 87 buckets; not quite the piano keys but close.
            val constantQ = ConstantQ(sampleRate, 40f, 6000f, 12f, 0.001f, 2.3f)
            logger.debug { "FFT length: ${constantQ.ffTlength}; bins: ${constantQ.numberOfOutputBands}" }
            val dataLineInfo = DataLine.Info(TargetDataLine::class.java, format)
            val line: TargetDataLine
            line = mixer.getLine(dataLineInfo) as TargetDataLine
            val bufferSize = constantQ.ffTlength
            numberOfBuckets = bufferSize
            line.open(format, numberOfBuckets)
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
                    listeners.forEach { it.onSample(analysis) }
                    return true
                }

                override fun processingFinished() {
                }
            })
            thread = thread(name = "JvmMediaDevices Audio Processor", isDaemon = true) {
                try {
                    dispatcher.run()
                } catch (t: Throwable) {
                    logger.error(t) { "audio processing failed" }
                }
            }
        }

        fun finalize() {
            close()
        }

        fun close() {
            dispatcher.stop()
            thread.join()
        }
    }
}