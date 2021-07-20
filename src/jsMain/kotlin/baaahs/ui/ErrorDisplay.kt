package baaahs.ui

import baaahs.window
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.container.container
import materialui.components.paper.paper
import materialui.icon
import org.w3c.dom.HTMLElement
import react.RProps
import react.dom.*
import react.functionalComponent
import styled.inlineStyles

val ErrorDisplay = functionalComponent<ErrorDisplayProps> { props ->
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

    paper {
        attrs.role = "alert"

        div {
            inlineStyles {
                backgroundColor = Color.black
                color = Color.red
                margin = "0"
                padding = ".5em"
            }

            div {
                ref = guruMediationBox

                inlineStyles {
                    margin = 1.em.toString()
                    padding = 1.em.toString()
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = Align.center
                    justifyContent = JustifyContent.spaceEvenly
                }

                span {
                    inlineStyles {
                        float = Float.left
                        paddingRight = 2.em
                    }
                    icon(materialui.icons.NotificationImportant)
                }

                div {
                    h2 { +"Something went horribly wrong." }

                    pre { +(props.error.message ?: "Unknown error") }
                }

                if (props.resetErrorBoundary != null) {
                    button {
                        inlineStyles {
                            border = "1px solid red"
                            color = Color.red
                        }

                        attrs.variant = ButtonVariant.outlined
                        attrs.onClickFunction = { props.resetErrorBoundary?.invoke() }

                        +"Press to retry."
                    }
                }
            }
        }

        container {
            h6 { +"Stack Trace" }
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

external interface ErrorDisplayProps : RProps {
    var error: Error
    var componentStack: String
    var resetErrorBoundary: (() -> Unit)?
}

