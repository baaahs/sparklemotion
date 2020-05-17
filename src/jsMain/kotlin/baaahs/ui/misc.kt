package baaahs.ui

import react.RBuilder
import react.RComponent
import react.RProps
import react.createElement

// TODO: we shouldn't need to repeat ourselves so much when calling this...
inline fun <reified T : RComponent<P, *>, reified P : RProps> RBuilder.add(
    props: P,
    noinline block: RBuilder.() -> Unit = {}
) {
    child(createElement(T::class.js, props, block))
}
