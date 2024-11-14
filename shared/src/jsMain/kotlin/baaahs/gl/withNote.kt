package baaahs.gl

actual fun <T : Any?> T.withNote(note: String): T {
    try {
        this?.asDynamic()?._note_ = note
    } catch (e: Throwable) {
    }
    return this
}

actual fun <T: Any?> T.toStringMaybeWithNote(): String {
    val note = this?.asDynamic()?._note_
    return note ?: this.toString()
}