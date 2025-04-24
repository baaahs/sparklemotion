package baaahs.ui

import external.react_draggable.Draggable
import mui.material.Paper
import mui.material.PaperProps
import react.fc
import react.useRef
import web.html.HTMLElement

val DraggablePaperHandleClassName = "DraggablePaper-handle"

val DraggablePaper = fc<PaperProps> { props ->
    val draggableRef = useRef<HTMLElement>()

    Draggable {
        attrs.nodeRef = draggableRef
        attrs.handle = ".${DraggablePaperHandleClassName}"
        attrs.cancel = "[class*=\"MuiDialogContent-root\"]"
        Paper {
            copyFrom(props)
        }
    }
}