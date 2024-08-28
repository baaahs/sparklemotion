package baaahs.plugin.osc

import baaahs.plugin.sound_analysis.AnalyzerStream
import baaahs.util.Logger
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPort
import com.illposed.osc.transport.OSCPortIn

actual class OscBridge {
    private val oscPort      : Int = 7777
    private val palette      : Array<baaahs.Color> = Array(5) { baaahs.Color.BLACK }
    private val addressRegex : Regex = Regex("Color([0-4])_([rgb])", RegexOption.IGNORE_CASE)
    private val selector     : OSCPatternAddressMessageSelector = OSCPatternAddressMessageSelector("/SonicRunway/Color?_?")

    private lateinit var receiver     : OSCPortIn
    actual fun start() {
        receiver = OSCPortIn(oscPort)

        val listener = OSCMessageListener { event ->
            val result = addressRegex.find(event.message.address)
            if (result != null) {
                val (index, component) = result.destructured
                val idx = index.toInt()
                val value : Byte = (255.0 * event.message.arguments[0] as Float).toInt().toByte()
                when(component) {
                    "r" -> palette[idx] = palette[idx].withRed(value)
                    "g" -> palette[idx] = palette[idx].withGreen(value)
                    "b" -> palette[idx] = palette[idx].withBlue(value)
                }

                println("OscBridge: ${event.time} : [${idx}] ${palette[idx]}")
            }
        }


        receiver.isResilient = true
        receiver.dispatcher.addListener(selector, listener)
        receiver.dispatcher.isAlwaysDispatchingImmediately = true
        receiver.startListening()
        println("OscBridge: OSC started! $receiver")
    }

    companion object {
        private val logger = Logger<OscBridge>()
    }
}

// 192.168.0.31 255:255:255:0 port 2390