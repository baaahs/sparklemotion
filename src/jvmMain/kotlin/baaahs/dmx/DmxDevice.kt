package baaahs.dmx

import baaahs.Dmx
import com.ftdichip.ftd2xx.*

class DmxDevice(private val usbDevice: Device) : Dmx.Universe() {
    val buf = ByteArray(513) // DMX wants byte 0 to always be 0

    init {
        try {
            usbDevice.open()
            configureDmx()
        } catch (e: FTD2xxException) {
            e.printStackTrace()
        }
    }

    override fun writer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
        Dmx.Buffer(buf, baseChannel, channelCount)

    override fun sendFrame() {
        send(buf, usbDevice)
    }

    override fun allOff() {
        buf.fill(0)
    }

    private fun configureDmx() {
        val port = usbDevice.getPort()
        port.setDivisor(12)
        port.setDataCharacteristics(DataBits.DATA_BITS_8, StopBits.STOP_BITS_2, Parity.NONE)
        port.setFlowControl(FlowControl.NONE)
        port.setRTS(false)
        usbDevice.latencyTimer = 40
        usbDevice.purgeReceiveBuffer()
        usbDevice.purgeTransmitBuffer()
    }

    @Throws(FTD2xxException::class)
    private fun send(bytes: ByteArray, device: Device) {
        val port = device.port
        port.setBreakOn(true)
        port.setBreakOn(false)
        device.write(bytes)
    }

    private fun sleep(millis: Int) {
        try {
            Thread.sleep(millis.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun listDevices() = Service.listDevicesByType(DeviceType.FT_DEVICE_232R).map { DmxDevice(it) }
    }

}
