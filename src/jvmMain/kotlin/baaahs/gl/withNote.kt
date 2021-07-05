package baaahs.gl

actual fun <T> T.withNote(note: String): T = this
actual fun <T: Any?> T.toStringMaybeWithNote(): String = toString()