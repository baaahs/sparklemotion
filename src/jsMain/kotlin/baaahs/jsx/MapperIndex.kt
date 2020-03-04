@file:JsModule("js/mapper/index.jsx")
@file:JsNonModule

package baaahs.jsx

import info.laht.threekt.cameras.PerspectiveCamera
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import react.RProps

@JsName("default")
external class MapperIndex(props: Props) {
    companion object {
        fun createCameraControls(uiCamera: PerspectiveCamera, domElement: Node): dynamic
    }

    interface Props : RProps {
        var render: (parentNode: HTMLElement) -> Unit
    }
}
