package baaahs.show.live

expect interface ControlView

expect fun getViewFor(openControl: OpenControl): ControlView
