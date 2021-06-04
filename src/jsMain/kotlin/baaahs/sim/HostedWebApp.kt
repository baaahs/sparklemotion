package baaahs.sim

import react.ReactElement

interface HostedWebApp {
    @JsName("onLaunch")
    fun onLaunch() {}

    @JsName("render")
    fun render(): ReactElement

    @JsName("onClose")
    fun onClose()
}
