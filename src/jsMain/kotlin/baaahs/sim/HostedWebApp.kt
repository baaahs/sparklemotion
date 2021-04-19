package baaahs.sim

import react.ReactElement

external interface HostedWebApp {
    @JsName("render")
    fun render(): ReactElement

    @JsName("onClose")
    fun onClose()
}
