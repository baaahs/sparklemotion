package baaahs.dmx

import com.ftdichip.ftd2xx.DeviceType
import com.ftdichip.ftd2xx.Service

object JvmFtdiDmxDriver : Dmx.Driver {
    override fun findDmxDevices(): List<Dmx.Device> {
        return try {
            Service.listDevicesByType(DeviceType.FT_DEVICE_232R).map { JvmDmxDevice(it) }
        } catch (e: UnsatisfiedLinkError) {
            DmxManagerImpl.logger.warn { "DMX driver not found, DMX will be disabled." }
            e.printStackTrace()
            emptyList()
        }
    }
}