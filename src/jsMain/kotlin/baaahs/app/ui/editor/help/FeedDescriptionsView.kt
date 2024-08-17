package baaahs.app.ui.editor.help

import baaahs.app.ui.appContext
import baaahs.ui.markdown
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.div
import react.useContext

private val FeedDescriptionsView = xComponent<FeedDescriptionsProps>("FeedDescriptions") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.shaderHelp

    Table {
        attrs.size = Size.small
        attrs.stickyHeader = true

        TableHead {
            TableRow {
                TableCell { +"Feed" }
                TableCell { +"Description" }
                TableCell { +"Content Type" }
            }
        }

        TableBody {
            appContext.plugins.feedBuilders.withPlugin
                .filterNot { (_, v) -> v.internalOnly }
                .sortedBy { (_, v) -> v.title }
                .forEach { (plugin, feedBuilder) ->
                    TableRow {
                        TableCell { +feedBuilder.title }
                        val contentType = feedBuilder.contentType
                        TableCell {
                            markdown {
                                +feedBuilder.description
                                    .replace("\n", "\n<br/>")
                            }
                            div(+styles.codeContainer) {
                                feedDeclaration {
                                    attrs.plugin = plugin
                                    attrs.feedBuilder = feedBuilder
                                }
                            }
                        }
                        TableCell { code { +contentType.id } }
                    }
                }
        }
    }
}

external interface FeedDescriptionsProps : Props {
}

fun RBuilder.feedDescriptions(handler: RHandler<FeedDescriptionsProps>) =
    child(FeedDescriptionsView, handler = handler)