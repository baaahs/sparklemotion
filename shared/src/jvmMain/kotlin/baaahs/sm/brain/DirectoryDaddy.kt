package baaahs.sm.brain

import baaahs.io.Fs
import baaahs.util.Logger

class DirectoryDaddy(val firmwareDir: Fs.File, val urlBase: String) : FirmwareDaddy {
    private var preferredVersion: String? = null

    override suspend fun start() {
        // TODO: Watch the directory for changes instead of just scanning once at startup
        preferredVersion = findPreferredFirmware()
        logger.debug { "Full URL is $urlForPreferredVersion" }
    }

    private suspend fun findPreferredFirmware(): String? {
        try {
            val files = firmwareDir.listFiles()
            logger.debug { "Found the following firmware files:" }

            var currentNum = 0
            var currentFile: Fs.File? = null

            for (f in files) {
                if (!f.name.endsWith(".bin")) continue

                println("  $f");

                val tokens = f.name.split("-")
                if (tokens.size > 2) {
                    val num = tokens[1].toInt()
                    if (num > currentNum) {
                        currentNum = num
                        currentFile = f
                    }
                }
            }

            if (currentFile == null) {
                logger.warn { "  ** No .bin file is named with a proper firmware version like '0.0.1-450-gad9451b-dirty'" }
            } else {
                logger.debug { "Selected firmware ====> $currentFile" }
                return currentFile.name.substring(0, currentFile.name.length - 4);
            }
        } catch (e: Exception) {
            // Probably the directory doesn't exist
            logger.error(e) { "Exception encountered looking for a firmware to vend. No firmware will be distributed." }
        }
        return null
    }

    override fun doesntLikeThisVersion(firmwareVersion: String?): Boolean {
        // If we didn't find a firmware, don't hassle people. Accept anything.
        if (preferredVersion == null) return false

        return firmwareVersion != preferredVersion
    }

    override val urlForPreferredVersion: String
        get() = "$urlBase/$preferredVersion.bin"

    companion object {
        private val logger = Logger("DirectoryDaddy")
    }
}
