package baaahs.ui

import external.react_draggable.Draggable
import mui.material.Paper
import mui.material.PaperProps
import react.fc

val DraggablePaperHandleClassName = "DraggablePaper-handle"

val DraggablePaper = fc<PaperProps> { props ->
    Draggable {
        attrs.handle = ".${DraggablePaperHandleClassName}"
        attrs.cancel = "[class*=\"MuiDialogContent-root\"]"
        Paper {
            copyFrom(props)
        }
    }
}