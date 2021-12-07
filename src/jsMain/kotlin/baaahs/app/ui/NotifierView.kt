package baaahs.app.ui

import baaahs.client.Notifier
import baaahs.ui.markdown
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import materialui.components.backdrop.backdrop
import materialui.components.backdrop.enum.BackdropStyle
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import materialui.lab.components.alert.enums.AlertStyle
import materialui.lab.components.alerttitle.alertTitle
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
            backdrop(Styles.serverNoticeBackdrop on BackdropStyle.root) {
                attrs { open = true }

                div {
                    serverNotices.forEach { serverNotice ->
                        alert(Styles.serverNoticeAlertMessage on AlertStyle.message) {
                            attrs.severity = AlertSeverity.error
                            attrs.onClose = { notifier.confirmServerNotice(serverNotice.id) }

                            alertTitle {
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