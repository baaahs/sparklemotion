package baaahs.ui

import external.react_draggable.Draggable
import materialui.components.paper.PaperProps
import materialui.components.paper.paper
import react.RBuilder
import react.RComponent
import react.State

class DraggablePaper(props: PaperProps) : RComponent<PaperProps, State>(props) {
    override fun RBuilder.render() {
        Draggable {
            attrs.handle = ".$handleClassName"
            attrs.cancel = "[class*=\"MuiDialogContent-root\"]"
            paper {
                props(props)
            }
        }
    }

    companion object {
         val handleClassName = "DraggablePaper-handle"
    }
}
