package baaahs.ui

import baaahs.window
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.container.container
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.icon
import org.w3c.dom.HTMLElement
import react.Props
import react.dom.div
import react.dom.h2
import react.dom.pre
import react.dom.span
import react.functionComponent
import styled.inlineStyles

val ErrorDisplay = functionComponent<ErrorDisplayProps> { props ->
    val guruMediationBox = react.useRef<HTMLElement>(null)

    react.useEffect {
        var outlined = true
        fun blink() {
            window.setTimeout({
                val box = guruMediationBox.current
                if (box != null) {
                    box.style.border = if (outlined) "6px solid red" else "6px solid black"
                    outlined = !outlined
                    blink()
                }
            }, 1000)
        }
        blink()
    }

    container {
        attrs.role = "alert"
        inlineStyles {
            height = 100.vh
        }

        div(+Styles.guruMeditationErrorContainer) {
            div(+Styles.guruMeditationErrorBox) {
                ref = guruMediationBox

                span(+Styles.guruMeditationErrorIcon) {
                    icon(materialui.icons.NotificationImportant)
                }

                div {
                    h2 { +"Something went horribly wrong." }

                    pre { +(props.error.message ?: "Unknown error") }
                }

                if (props.resetErrorBoundary != null) {
                    button {
                        attrs.variant = ButtonVariant.outlined
                        attrs.onClickFunction = { props.resetErrorBoundary?.invoke() }

                        +"Press to retry."
                    }
                }
            }
        }

        paper(+Styles.guruMeditationErrorStackTrace on PaperStyle.root) {
            attrs.elevation = 5

            h2 { +"Stack Trace" }
            pre {
                inlineStyles {
                    paddingBottom = 2.em
                    overflow = Overflow.scroll
                }

                +props.componentStack
            }
        }
    }
}

external interface ErrorDisplayProps : Props {
    var error: Error
    var componentStack: String
    var resetErrorBoundary: (() -> Unit)?
}

