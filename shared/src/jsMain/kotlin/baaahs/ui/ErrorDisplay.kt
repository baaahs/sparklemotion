package baaahs.ui

import js.objects.jso
import kotlinx.css.*
import kotlinx.css.properties.deg
import kotlinx.css.properties.rotate
import materialui.icon
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Container
import mui.material.Paper
import react.Props
import react.dom.*
import react.dom.aria.AriaRole
import react.fc
import styled.inlineStyles
import web.cssom.vh
import web.html.HTMLElement
import web.timers.setTimeout

val ErrorDisplay = fc<ErrorDisplayProps> { props ->
    val guruMediationBox = react.useRef<HTMLElement>(null)

    react.useEffect {
        var outlined = true
        fun blink() {
            setTimeout({
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

    Container {
        attrs.sx = jso {
            height = 100.vh
        }
        attrs.role = AriaRole.alert

        div(+Styles.guruMeditationErrorContainer) {
            div(+Styles.guruMeditationErrorBox) {
                ref = guruMediationBox

                span(+Styles.guruMeditationErrorIcon) {
                    icon(mui.icons.material.NotificationImportant)
                }

                div {
                    h2 { +"Something went horribly wrong." }

                    pre {
                        inlineStyles {
                            whiteSpace = WhiteSpace.preWrap

                        }
                        +(props.error.message ?: "Unknown error")
                    }
                }

                if (props.resetErrorBoundary != null) {
                    Button {
                        attrs.variant = ButtonVariant.outlined
                        attrs.onClick = { props.resetErrorBoundary?.invoke() }

                        +"Press to retry."
                    }
                }
            }
        }

        Paper {
            attrs.className = -Styles.guruMeditationErrorStackTrace
            attrs.elevation = 5

            h3 {
                inlineStyles {
                    position = Position.absolute
                    transform.rotate((-90).deg)
                    declarations["transformOrigin"] = "top right"
                    left = 0.em
                }
                +"Stack Trace"
            }
            pre {
                inlineStyles {
                    paddingBottom = 2.em
                    height = 100.pct
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

