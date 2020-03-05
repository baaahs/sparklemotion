package baaahs.admin

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div

class AdminPage(props: Props) : RComponent<AdminPage.Props, AdminPage.State>(props) {
    private val container = react.createRef<HTMLDivElement>()

    override fun componentDidMount() {
        container.current?.appendChild(props.containerDiv)
        props.containerDiv.dispatchEvent(Event("resize"))
    }

    override fun componentWillUnmount() {
        container.current?.removeChild(props.containerDiv)
    }

    override fun RBuilder.render() {
        div { ref = container }
    }

    class Props(
        var containerDiv: HTMLDivElement
    ) : RProps

    class State : RState
}