@file:JsModule("js/mapper/index.jsx")
@file:JsNonModule

package baaahs.jsx

import org.w3c.dom.HTMLElement
import react.RProps

@JsName("default")
external class MapperIndex(props: Props) {
    interface Props : RProps {
        var render: (parentNode: HTMLElement) -> Unit
    }
}
