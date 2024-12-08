package baaahs.gl

actual fun <T : Any?> T.withNote(note: String): T {
    try {
        this?.asDynamic()?._note_ = note
    } catch (_: Throwable) {
        // Ignore if this fails because `this` isn't an ordinary JS object.
    }
    return this
}

actual fun <T: Any?> T.toStringMaybeWithNote(): String {
    val note = this?.asDynamic()?._note_
    return note ?: this.toString()
}