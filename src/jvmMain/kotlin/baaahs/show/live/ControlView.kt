package baaahs.show.live

actual interface ControlView

actual fun getViewFor(openControl: OpenControl): ControlView = error("Not available on JVM")