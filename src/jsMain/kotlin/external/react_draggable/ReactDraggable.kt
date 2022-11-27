@file:JsModule("react-draggable")

package external.react_draggable

import dom.html.HTMLElement
import org.w3c.dom.events.MouseEvent
import react.ElementType
import react.Props
import react.ReactElement
import react.Ref

@JsName("default")
external val Draggable : ElementType<DraggableProps>

external val DraggableCore : ElementType<DraggableCoreProps>

external interface DraggableCoreState {
    var dragging: Boolean
    var lastX: Number
    var lastY: Number
    var touchIdentifier: Number?
}

external interface DraggableBounds {
    var left: Number
    var right: Number
    var top: Number
    var bottom: Number
}

external interface DraggableData {
    var node: HTMLElement
    var x: Int
    var y: Int
    var deltaX: Int
    var deltaY: Int
    var lastX: Int
    var lastY: Int
}

external interface ControlPosition {
    var x: Number
    var y: Number
}

external interface PositionOffsetControlPosition {
    var x: Any // Number|string
    var y: Any // Number|string
}

external interface DraggableCoreProps: Props {
    var allowAnyClick: Boolean
    var cancel: String
    var children: ReactElement<*>
    var disabled: Boolean
    var enableUserSelectHack: Boolean
    var offsetParent: HTMLElement
    var grid: Array<Number>
    var handle: String
    var onStart: DraggableEventHandler
    var onDrag: DraggableEventHandler
    var onStop: DraggableEventHandler
    var onMouseDown: (e: MouseEvent) -> Unit
    var scale: Number
    var nodeRef: Ref<*>?
}

external interface DraggableProps: DraggableCoreProps {
    var axis: String // 'both' | 'x' | 'y' | 'none'
    var bounds: Any // DraggableBounds | string | false
    var defaultClassName: String
    var defaultClassNameDragging: String
    var defaultClassNameDragged: String
    var defaultPosition: ControlPosition
    var positionOffset: PositionOffsetControlPosition
    var position: ControlPosition
}

external interface RDraggableState {
    var dragging: Boolean
    var dragged: Boolean
    var x: Number
    var y: Number
    var slackX: Number
    var slackY: Number
    var isElementSVG: Boolean
}