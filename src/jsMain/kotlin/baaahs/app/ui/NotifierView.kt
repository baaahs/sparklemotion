package baaahs.app.ui

import baaahs.client.Notifier
import baaahs.ui.markdown
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import js.objects.jso
import mui.material.Alert
import mui.material.AlertColor
import mui.material.AlertTitle
import mui.material.Backdrop
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.pre

private val NotifierView = xComponent<NotifierProps>("Notifier") { props ->
    val notifier = props.notifier
    observe(notifier)

    notifier.serverNotices.let { serverNotices ->
        if (serverNotices.isNotEmpty()) {
            Backdrop {
                attrs.classes = jso { this.root = -Styles.serverNoticeBackdrop }
                attrs { open = true }

                div {
                    serverNotices.forEach { serverNotice ->
                        Alert {
                            attrs.classes = jso { this.message = -Styles.serverNoticeAlertMessage }

                            attrs.severity = AlertColor.error
                            attrs.onClose = { notifier.confirmServerNotice(serverNotice.id) }

                            AlertTitle {
                                +serverNotice.title
                            }

                            serverNotice.message?.let {
                                div(+Styles.serverNoticeMessage) {
                                    markdown { +it }
                                }
                            }

                            serverNotice.stackTrace?.let {
                                pre(+Styles.serverNoticeStackTrace) { +it }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface NotifierProps : Props {
    var notifier: Notifier.Facade
}

fun RBuilder.notifier(handler: RHandler<NotifierProps>) =
    child(NotifierView, handler = handler)