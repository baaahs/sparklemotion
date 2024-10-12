package baaahs.dmx

import com.ftdi.FTDevice

object JvmFtdiDmxDriver : Dmx.Driver {
    override fun findDmxDevices(): List<Dmx.Device> {
        return try {
            FTDevice.getDevices().map { JvmDmxDevice(it) }
        } catch (e: UnsatisfiedLinkError) {
            DmxManagerImpl.logger.warn { "DMX driver not found, DMX will be disabled." }
            e.printStackTrace()
            emptyList()
        }
    }
}