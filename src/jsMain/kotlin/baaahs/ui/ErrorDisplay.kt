package baaahs.ui

import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import materialui.components.button.button
import react.RProps
import react.dom.div
import react.dom.p
import react.dom.pre
import react.functionalComponent

val ErrorDisplay = functionalComponent<ErrorDisplayProps> { props ->
    div {
        attrs.role = "alert"
        p { +"Something went wrong:" }
        pre { +(props.error.message ?: "Unknown error") }
        pre { +props.componentStack }
        button { attrs.onClickFunction = { props.resetErrorBoundary() } }
    }
}

external interface ErrorDisplayProps : RProps {
    var error: Error
    var componentStack: String
    var resetErrorBoundary: () -> Unit
}

