@file:JsModule("js/MosaicUI.jsx")
@file:JsNonModule

package baaahs.jsx

import baaahs.SheepSimulator
import react.RProps

@JsName("default")
external class MosaicUI(props: Props) {
    interface Props : RProps {
        var getSheepSimulator: () -> SheepSimulator
    }
}
