package baaahs.plugin.osc

import baaahs.util.Logger
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPortIn

actual class OscBridge {
    private val oscPort      : Int = 7777 // 2390 in production!
    private val palette      : Array<baaahs.Color> = Array(5) { baaahs.Color.BLACK }
    private val addressRegex : Regex = Regex("Color([0-4])_([rgb])", RegexOption.IGNORE_CASE)
    private val selector     : OSCPatternAddressMessageSelector = OSCPatternAddressMessageSelector("/SonicRunway/Color?_?")
    private lateinit var receiver     : OSCPortIn

    private fun updateColor(idx : Int, k : String, value : Byte) : Boolean {
        var oldVal : Byte = 0
        when (k) {
            "r" -> {
                oldVal = palette[idx].redB
                palette[idx] = palette[idx].withRed(value)
            }
            "g" -> {
                oldVal = palette[idx].greenB
                palette[idx] = palette[idx].withGreen(value)
            }
            "b" -> {
                oldVal = palette[idx].blueB
                palette[idx] = palette[idx].withBlue(value)
            }
        }
        return oldVal != value
    }

    actual fun start() {
        receiver = OSCPortIn(oscPort)

        val listener = OSCMessageListener { event ->
            val result = addressRegex.find(event.message.address)
            if (result != null) {
                val (index, component) = result.destructured
                val idx = index.toInt()
                val value : Byte = (255.0 * event.message.arguments[0] as Float).toInt().toByte()
                if (updateColor(idx, component, value)) {
                    // TODO: Send it to SparkleMotion
                    println("OscBridge: ${event.time} : [${idx}] ${palette[idx]}")
                }
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

// Our very own static IP on Sonic Runway's network: 192.168.0.31 netmask: 255:255:255:0