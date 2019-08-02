package baaahs.sim

import baaahs.io.Fs
import baaahs.logger

class FakeFs : Fs {
    override fun loadFile(name: String): String? {
        logger.debug("FakeFs.loadFile($name)")
        return null
    }

    override fun createFile(name: String, content: ByteArray, allowOverwrite: Boolean) {
        logger.debug("FakeFs.createFile($name) -> $content")
    }

    override fun createFile(name: String, content: String, allowOverwrite: Boolean) {
        logger.debug("FakeFs.createFile($name) -> $content")
    }
}