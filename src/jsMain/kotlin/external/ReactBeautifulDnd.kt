package external

import js.core.jso
import org.w3c.dom.events.Event
import react.*
import react.dom.RDOMBuilder
import react.dom.setProp

@JsModule("js/lib/react-beautiful-dnd-13.0-fixed.js")
private external val reactBeautifulDndModule: dynamic

external interface DragDropContextProps : Props, Responders {
    // Read out by screen readers when focusing on a drag handle
    var dragHandleUsageInstructions: String?
    // Used for strict content security policies
    // See our [content security policy guide](/docs/guides/content-security-policy.md)
    var nonce: String?
    // See our [sensor api](/docs/sensors/sensor-api.md)
//    sensors?: Sensor[],
    var enableDefaultSensors: Boolean?
}

@Suppress("UnsafeCastFromDynamic")
private val DragDropContext: FC<DragDropContextProps> = reactBeautifulDndModule.DragDropContext

fun RBuilder.dragDropContext(
    attrs: DragDropContextProps.() -> Unit,
    children: RBuilder.() -> Any
) =
    child(DragDropContext, jso<DragDropContextProps>().apply { attrs() }) {
        children()
    }

external interface DraggableProps : Props {
    var draggableId: String
    var index: Int
    var isDragDisabled: Boolean
    var onTransitionEnd: ((Event) -> Unit)?
    var dragHandleProps: DragHandleProps

}

@Suppress("UnsafeCastFromDynamic")
private val Draggable: FC<DraggableProps> = reactBeautifulDndModule.Draggable

fun RBuilder.draggable(
    attrs: DraggableProps.() -> Unit,
    children: (provided: DraggableProvided, snapshot: DraggableStateSnapshot) -> ReactElement<*>
) {
    child(Draggable, jso<DraggableProps>().apply { attrs() }) {
        child(children.unsafeCast<ReactNode>())
    }
}

external interface DraggableStateSnapshot {
    var isDragging: Boolean
}

private val jsObj = js("Object")

fun RElementBuilder<*>.copyFrom(fromProps: Props?) {
    copyFrom(fromProps.unsafeCast<CopyableProps>())
}

fun RElementBuilder<*>.copyFrom(fromProps: CopyableProps?) {
    if (fromProps == null) return

    val from = fromProps.asDynamic()
    val keys = jsObj.keys(fromProps).unsafeCast<Array<String>>()
    keys.forEach { key -> attrs.asDynamic()[key] = from[key] }
}

fun RDOMBuilder<*>.copyFrom(fromProps: CopyableProps?) {
    if (fromProps == null) return

    val from = fromProps.asDynamic()
    val keys = jsObj.keys(fromProps).unsafeCast<Array<String>>()
    keys.forEach { key -> setProp(key, from[key]) }
}

external interface Responders {
    var onBeforeCapture: OnBeforeCaptureResponder?
    var onBeforeDragStart: OnBeforeDragStartResponder?
    var onDragStart: OnDragStartResponder?
    var onDragUpdate: OnDragUpdateResponder?

    // always require
    var onDragEnd: OnDragEndResponder
}

external interface CopyableProps

external interface DragHandleProps: CopyableProps {
    var draggable: Boolean
    var onDragStart: (Event) -> Unit
    var role: String
}

external interface DroppableProvided: CopyableProps {
    var droppableProps: CopyableProps
    var placeholder: ReactNode
    var innerRef: Ref<*>
}

external interface DraggableProvided: CopyableProps {
    var draggableProps: CopyableProps
    var dragHandleProps: CopyableProps
    var innerRef: Ref<*>
}

@Suppress("EnumEntryName")
enum class Direction {
    vertical, horizontal
}

external interface Combine {
    var draggableId: DraggableId
    var droppableId: DroppableId
}

external interface DroppableProps : Props, CopyableProps {
    var droppableId: DroppableId
    var type: TypeId
    var isDropDisabled: Boolean
    var direction: String // should be Direction
    var dragHandleProps: DragHandleProps
}

external interface BeforeCapture {
    var draggableId: DraggableId
    var mode: MovementMode
}

external interface DraggableLocation {
    var droppableId: DroppableId
    var index: Int
}

external interface DraggableRubric {
    var draggableId: DraggableId
    var type: TypeId
    var source: DraggableLocation
}

external interface DragStart : DraggableRubric {
    var mode: MovementMode
}

external interface DragUpdate : DragStart {
    var destination: DraggableLocation?
    var combine: Combine?
}

enum class DropReason {
    DROP, CANCEL
}

external interface DropResult : DragUpdate {
    var reason: String // DropReason
}

typealias TypeId = String
typealias DraggableId = String
typealias DroppableId = String

typealias Announce = (message: String) -> Unit

external interface ResponderProvided {
    var announce: Announce
}

typealias OnBeforeCaptureResponder = (before: BeforeCapture) -> Unit
typealias OnBeforeDragStartResponder = (start: DragStart) -> Unit
typealias OnDragStartResponder = (start: DragStart, provided: ResponderProvided) -> Unit
typealias OnDragUpdateResponder = (update: DragUpdate, provided: ResponderProvided) -> Unit
typealias OnDragEndResponder = (result: DropResult, provided: ResponderProvided) -> Unit

enum class MovementMode {
    FLUID, SNAP
}

@Suppress("UnsafeCastFromDynamic")
private val Droppable: FC<DroppableProps> = reactBeautifulDndModule.Droppable

fun RBuilder.droppable(
    attrs: DroppableProps.() -> Unit,
    children: (provided: DroppableProvided, snapshot: Any) -> ReactElement<*>
) {
    child(
        Droppable,
        jso<DroppableProps>().apply { attrs() }
    ) {
        childList.add(children as ReactNode)
    }
}

private val noOpDroppableProvided = jso<DroppableProvided> {
}
