package baaahs.sm.brain

interface FirmwareDaddy {
    fun doesntLikeThisVersion(firmwareVersion: String?): Boolean

    val urlForPreferredVersion: String

    suspend fun start() {}
}

class PermissiveFirmwareDaddy : FirmwareDaddy {
    override fun doesntLikeThisVersion(firmwareVersion: String?): Boolean {
        // False indicates this permissive daddy likes all firmwares
        return false;
    }

    override val urlForPreferredVersion: String
        get() = ""
}

class StrictFirmwareDaddy(private val version: String, private val url: String) : FirmwareDaddy {
    override fun doesntLikeThisVersion(firmwareVersion: String?): Boolean {
        return version == firmwareVersion;
    }

    override val urlForPreferredVersion: String
        get() = url
}
