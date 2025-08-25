package baaahs.dmx

import com.ftdichip.ftd2xx.*

class JvmDmxDevice(usbDevice: Device) : Dmx.Device {
    override val id: String = "usb id${usbDevice.deviceDescriptor.serialNumber}"
    override val name: String = "usb name ${usbDevice.deviceDescriptor.productDescription}"

    private val buf = ByteArray(513) // DMX wants byte 0 to always be 0
    private val dmxTransmitter = DmxTransmitter(usbDevice).apply { start() }

    override fun asUniverse(
        universeListener: Dmx.UniverseListener?
    ): Dmx.Universe = object : Dmx.Universe() {
        override fun writer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
            Dmx.Buffer(buf, baseChannel, channelCount)

        override fun sendFrame() {
            dmxTransmitter.update(buf)
            universeListener?.onSend(id, buf)
        }

        override fun allOff() {
            buf.fill(0)
        }
    }

    fun close() {
        dmxTransmitter.keepRunning = false
        dmxTransmitter.join()
    }

    fun finalize() {
        close()
    }

    private class DmxTransmitter(private val device: Device) : Thread("DMXTransmitter") {
        val DMX_TRANSMIT_DELAY = 20

        var keepRunning = true
        private var updated = false
        private val newData: ByteArray = ByteArray(513)
        private val currentData: ByteArray = ByteArray(513)

        init {
            device.open()
            configureDmx()
        }

        private fun configureDmx() {
            val port = device.getPort()
            port.setDivisor(12)
            port.setDataCharacteristics(DataBits.DATA_BITS_8, StopBits.STOP_BITS_2, Parity.NONE)
            port.setFlowControl(FlowControl.NONE)
            port.setRTS(false)
            device.latencyTimer = 40
            device.purgeReceiveBuffer()
            device.purgeTransmitBuffer()
        }

        @Synchronized
        fun update(buf: ByteArray) {
            buf.copyInto(newData)
            updated = true
        }

        override fun run() {
            while (keepRunning) {
                fillNewDataIfUpdated()
                transmit(currentData)
            }
        }

        @Synchronized
        private fun fillNewDataIfUpdated() {
            if (updated) {
                newData.copyInto(currentData)
                updated = false
            }
        }

        @Throws(FTD2xxException::class)
        private fun transmit(bytes: ByteArray) {
            val port = device.port
            port.setBreakOn(true)
            port.setBreakOn(false)
            device.write(bytes)

            sleep(DMX_TRANSMIT_DELAY)
        }

        @Suppress("RemoveRedundantQualifierName", "SameParameterValue")
        private fun sleep(millis: Int) {
            try {
                Thread.sleep(millis.toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}
