package baaahs.ui

import external.react_draggable.Draggable
import materialui.components.paper.PaperProps
import materialui.components.paper.paper
import react.RBuilder
import react.RComponent
import react.RState

class DraggablePaper(props: PaperProps) : RComponent<PaperProps, RState>(props) {
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
