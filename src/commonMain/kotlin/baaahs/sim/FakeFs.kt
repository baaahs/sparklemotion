package baaahs.sim

import baaahs.io.Fs
import baaahs.logger

class FakeFs : Fs {
    override fun createFile(name: String, content: ByteArray) {
        logger.debug("FakeFs.createFile($name) -> $content")
    }

    override fun createFile(name: String, content: String) {
        logger.debug("FakeFs.createFile($name) -> $content")
    }
}