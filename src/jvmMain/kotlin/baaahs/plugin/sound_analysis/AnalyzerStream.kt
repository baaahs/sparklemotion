package baaahs.plugin.sound_analysis

import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import baaahs.util.asDoubleSeconds
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.ConstantQ
import be.tarsos.dsp.GainProcessor
import be.tarsos.dsp.filters.HighPass
import be.tarsos.dsp.filters.LowPassSP
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import be.tarsos.dsp.util.fft.FFT
import javax.sound.sampled.*
import kotlin.concurrent.thread

class AnalyzerStream(
    mixer: Mixer,
    audioFormat: AudioFormat,
    private val constantQ: ConstantQ,
    private val clock: Clock,
    private val onProcess: (magnitudes: FloatArray, kick: Float, spectrum: FloatArray, time: Time) -> Unit
) {
    private val dispatcher: AudioDispatcher
    private val srDispatcher: AudioDispatcher
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
        srDispatcher = AudioDispatcher(audioStream, 2048, 2048/2) // 200ms

        var kick = 0.0F
        val spectrum = FloatArray(100)
        var magnitudes = FloatArray(0)
        var time : Time = 0.0

        dispatcher.addAudioProcessor(object : AudioProcessor {
            override fun process(audioEvent: AudioEvent): Boolean {
                time = clock.now().asDoubleSeconds
                constantQ.process(audioEvent)
                magnitudes = constantQ.magnitudes.copyOf()
                onProcess(magnitudes, kick, spectrum, time)
                return true
            }

            override fun processingFinished() {
            }
        })

        // Sonic Runway
        val highPass = HighPass(80F, 44100F)
        val loPass = LowPassSP(100F, 44100F)
        val gain = GainProcessor(0.2)
        val fft = FFT(128)



        srDispatcher.addAudioProcessor(object: AudioProcessor {
            override fun process(audioEvent:AudioEvent): Boolean {
                // spectrum
                val buffer: FloatArray = audioEvent.floatBuffer
                fft.forwardTransform(buffer)
                for (i in 28..<buffer.size) {
                    spectrum[i-28] = buffer[i]
                }

                // kick
                highPass.process(audioEvent)
                loPass.process(audioEvent)
                gain.process(audioEvent)
                kick = audioEvent.rms.toFloat()

                onProcess(magnitudes, kick, spectrum, time)
                return true
            }

            override fun processingFinished() {
            }
        })

        thread = thread(name = "JvmMediaDevices Audio Processor", isDaemon = true) {
            try {
                dispatcher.run()
                srDispatcher.run()
            } catch (t: Throwable) {
                logger.error(t) { "audio processing failed" }
            }
        }
    }

    fun release() {
        dispatcher.stop()
        srDispatcher.stop()
        thread.join()
    }

    companion object {
        private val logger = Logger<AnalyzerStream>()
    }
}