package baaahs.ui

import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import materialui.NotificationImportant
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import materialui.components.container.container
import materialui.components.paper.paper
import materialui.icon
import react.RProps
import react.dom.*
import react.functionalComponent

val ErrorDisplay = functionalComponent<ErrorDisplayProps> { props ->
    paper {
        attrs.role = "alert"

        container {
            icon(NotificationImportant)

            h2 { +"Something went wrong:" }

            button {
                attrs.variant = ButtonVariant.outlined
                attrs.color = ButtonColor.primary
                attrs.onClickFunction = { props.resetErrorBoundary() }
                +"Retry"
            }

            pre { +(props.error.message ?: "Unknown error") }

            props.error.cause?.let {
                h6 { +"Cause" }
                pre { +(it.toString()) }
            }

            h6 { +"Component Stack" }
            pre { +props.componentStack }
        }
    }
}

external interface ErrorDisplayProps : RProps {
    var error: Error
    var componentStack: String
    var resetErrorBoundary: () -> Unit
}

