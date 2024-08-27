package baaahs.plugin.osc

import baaahs.plugin.sound_analysis.AnalyzerStream
import baaahs.util.Logger
import com.illposed.osc.OSCMessageListener
import com.illposed.osc.messageselector.OSCPatternAddressMessageSelector
import com.illposed.osc.transport.OSCPort
import com.illposed.osc.transport.OSCPortIn

actual class OscBridge {
    private val oscPort: Int = OSCPort.DEFAULT_SC_OSC_PORT

    private lateinit var receiver: OSCPortIn

    actual fun start() {
        println("OscBridge: OSC starting...")
        receiver = OSCPortIn(oscPort)


        /*
         * OSCMessageListener listener = new OSCMessageListener() {
 *   public void acceptMessage(OSCMessageEvent event) {
 *     System.out.println("Message received: " + event.getMessage().getAddress());
 *   }
 * };
 * MessageSelector selector = new OSCPatternAddressMessageSelector(
 *     "/message/receiving");
 * receiver.getDispatcher().addListener(selector, listener);
 * // NOTE You might want to use this code, in case you have bundles
 * //      with time-stamps in the future, which you still want
 * //      to process immediately.
 * //receiver.getDispatcher().setAlwaysDispatchingImmediately(true);
 * receiver.startListening();
         */

        val listener = OSCMessageListener { event ->
            println("OscBridge: ${event.message.toString()}")
        }

        receiver.dispatcher.addListener(OSCPatternAddressMessageSelector("*"), listener)
        receiver.startListening()
        println("OscBridge: OSC started! $receiver")
    }

    companion object {
        private val logger = Logger<OscBridge>()
    }
}