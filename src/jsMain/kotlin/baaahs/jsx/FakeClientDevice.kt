@file:JsModule("js/FakeClientDevice.jsx")
@file:JsNonModule

package baaahs.jsx

import baaahs.HostedWebApp
import org.w3c.dom.HTMLElement
import react.RProps

@JsName("default")
external class FakeClientDevice(props: Props) {
    fun render(parentNode: HTMLElement)
    fun onClose()

    interface Props : RProps {
        var name: String
        var width: Int
        var height: Int
        var hostedWebApp: HostedWebApp
        var onClose: () -> Unit
    }
}
