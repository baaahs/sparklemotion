@file:JsModule("react-error-boundary")

package external

import react.ElementType
import react.Props

external val ErrorBoundary: ElementType<ErrorBoundaryProps>

external interface ErrorBoundaryProps: Props {
    var FallbackComponent: Any
}
