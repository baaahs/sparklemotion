@file:JsModule("react")

package external.react

import react.FunctionalComponent
import react.RProps

external fun <P : RProps> memo(
    fc: FunctionalComponent<P>,
    compare: (prevProps: P, nextProps: P) -> Boolean
): FunctionalComponent<P>
