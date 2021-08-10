package baaahs.monitor

import baaahs.ui.BComponent
import baaahs.ui.Observable
import baaahs.ui.on
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerClient
import kotlinx.css.margin
import kotlinx.css.zIndex
import materialui.components.backdrop.backdrop
import materialui.components.backdrop.enum.BackdropStyle
import materialui.components.circularprogress.circularProgress
import materialui.components.container.container
import materialui.components.container.enums.ContainerMaxWidth
import materialui.components.container.enums.ContainerStyle
import materialui.components.paper.paper
import materialui.components.typography.typographyH6
import materialui.icon
import org.w3c.dom.HTMLDivElement
import react.RBuilder
import react.RProps
import react.RState
import react.dom.div
import styled.StyleSheet

class MonitorPage(props: Props) : BComponent<MonitorPage.Props, MonitorPage.State>(props) {
    private val container = react.createRef<HTMLDivElement>()

    override fun observing(props: Props, state: State): List<Observable?> {
        return listOf(props.client)
    }

    override fun componentDidMount() {
        container.current?.appendChild(props.containerDiv)
        props.visualizer.resize()
    }

    override fun componentWillUnmount() {
        container.current?.removeChild(props.containerDiv)
    }

    override fun RBuilder.render() {
        paper {
            div { ref = container }

            if (!props.client.isConnected) {
                backdrop(Styles.root on BackdropStyle.root) {
                    attrs {
                        open = true
                    }

                    container(Styles.container on ContainerStyle.root) {
                        attrs.maxWidth = ContainerMaxWidth.md

                        circularProgress {}
                        icon(materialui.icons.NotificationImportant)

                        typographyH6 { +"Connecting…" }
                        +"Attempting to connect to Sparkle Motion."
                    }
                }
            }
        }

    }

    class Props(
        var containerDiv: HTMLDivElement,
        var visualizer: Visualizer,
        var client: RemoteVisualizerClient.Facade
    ) : RProps

    class State : RState
}

object Styles : StyleSheet("monitor", isStatic = true) {
    val root by css {
        zIndex = 1
    }

    val container by css {
        margin = "auto"
    }
}