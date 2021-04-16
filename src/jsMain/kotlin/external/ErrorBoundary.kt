@file:JsModule("react-error-boundary")

package external

import react.RClass
import react.RProps

external val ErrorBoundary: RClass<ErrorBoundaryProps>

external interface ErrorBoundaryProps: RProps {
    var FallbackComponent: Any
}
