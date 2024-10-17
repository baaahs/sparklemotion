package baaahs.sim

import react.ReactElement

interface HostedWebApp {
    fun onLaunch() {}

    fun render(): ReactElement<*>

    fun onClose()
}
