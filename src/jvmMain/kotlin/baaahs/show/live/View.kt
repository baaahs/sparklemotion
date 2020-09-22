package baaahs.show.live

actual interface View

actual fun getViewFor(openControl: OpenControl): View = error("Not available on JVM")