package baaahs.plugin.sound_analysis

import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asDoubleSeconds
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.ConstantQ
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import javax.sound.sampled.*
import kotlin.concurrent.thread

class AnalyzerStream(
    mixer: Mixer,
    audioFormat: AudioFormat,
    private val constantQ: ConstantQ,
    private val clock: Clock,
    private val onProcess: (magnitudes: FloatArray, time: Time) -> Unit
) {
    private val dispatcher: AudioDispatcher
    private val thread: Thread

    init {
        val dataLineInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
        val line = mixer.getLine(dataLineInfo) as TargetDataLine
        val bufferSize = constantQ.ffTlength

        line.open(audioFormat, bufferSize)
        val stream = AudioInputStream(line)
        line.start()
        val audioStream = JVMAudioInputStream(stream)

        // create a new dispatcher
        val bufferOverlap = bufferSize / 2
        dispatcher = AudioDispatcher(audioStream, bufferSize, bufferOverlap)

        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun process(audioEvent: AudioEvent): Boolean {
                val time = clock.now().asDoubleSeconds
                constantQ.process(audioEvent)
                val magnitudes = constantQ.magnitudes.copyOf()
                onProcess(magnitudes, time)
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

    fun release() {
        dispatcher.stop()
        thread.join()
    }

    companion object {
        private val logger = Logger<AnalyzerStream>()
    }
}