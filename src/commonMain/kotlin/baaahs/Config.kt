package baaahs

public class Config {
    companion object {
        val DMX_DEVICES: Map<String, Int> = mapOf(
            Pair("leftEye", 1),
            Pair("rightEye", 17)
        )
    }


    class MovingHeadConfig(val deviceType: Dmx.DeviceType, val baseChannel: Int)
}
