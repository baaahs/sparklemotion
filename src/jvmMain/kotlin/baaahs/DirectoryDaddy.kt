package baaahs

class DirectoryDaddy(fs: RealFs, val urlBase: String) : FirmwareDaddy {
    private var preferredVersion = ""

    init {
        // TODO: Watch the directory for changes instead of just scanning once at startup
        try {
            val files = fs.listFiles("")
            logger.info { "Found the following firmware files:" }

            var currentNum = 0L
            var currentFile: String? = null

            for (f in files) {
                try {
                    if (!f.endsWith(".bin")) continue

                    logger.info { "  $f" }

                    val tokens = f.split("-")
                    if (tokens.size > 1) {
                        val num = tokens[0].toLong()
                        if (num > currentNum) {
                            currentNum = num
                            currentFile = f
                        }
                    }
                } catch (e: Exception) {
                    logger.error("for $f", e)
                }
            }

            if (currentFile == null) {
                println("  ** No .bin file is named with a proper firmware version like '0.0.1-450-gad9451b-dirty'")
            } else {
                println("Selected firmware ====> $currentFile")

                preferredVersion = currentFile.substring(0, currentFile.length - 4)

                logger.info { "Full URL is $urlForPreferredVersion" }
            }
        } catch (e: Exception) {
            // Probably the directory doesn't exist
            logger.error("Exception encountered looking for a firmware to vend. No firmware will be distributed.", e)
        }
    }

    override fun doesntLikeThisVersion(firmwareVersion: String?): Boolean {
        // If we didn't find a firmware, don't hassle people. Accept anything.
        if (preferredVersion.isEmpty()) return false

        return firmwareVersion != preferredVersion
    }

    override val urlForPreferredVersion: String
        get() = "$urlBase/$preferredVersion.bin"

}
