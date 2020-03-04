@file:JsModule("js/app/index.jsx")
@file:JsNonModule

package baaahs.jsx

import baaahs.PubSub
import react.RProps

@JsName("default")
external class AppIndex(props: Props) {
    interface Props : RProps {
        var pubSub: PubSub.Client
    }
}
