@file:JsModule("react-mosaic-component")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package external.mosaic

import dom.Element
import react.Component
import react.Props
import react.State


external interface MosaicBaseProps<T>: Props {
    var renderTile: TileRenderer<T>
    var onChange: ((newNode: dynamic /* MosaicParent<T> | T */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onRelease: ((newNode: dynamic /* MosaicParent<T> | T */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var resize: dynamic /* String | EnabledResizeOptions */
        get() = definedExternally
        set(value) = definedExternally
    var zeroStateView: Element?
        get() = definedExternally
        set(value) = definedExternally
    var mosaicId: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MosaicControlledProps<T> : MosaicBaseProps<T> {
    var value: MosaicParent<T>? // | T
        get() = definedExternally
        set(value) = definedExternally
}

external interface MosaicUncontrolledProps<T> : MosaicBaseProps<T> {
    var initialValue: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
}

external interface MosaicState<T> {
    var currentNode: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
    var lastInitialValue: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
    var mosaicId: String
}

external interface MosaicStatePartial<T> {
    var currentNode: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
    var lastInitialValue: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
    var mosaicId: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var onChange: () -> Nothing?
    var zeroStateView: Element
    var className: String
}

//external open class MosaicWithoutDragDropContext<T> : React.PureComponent<dynamic /* MosaicControlledProps<T> | MosaicUncontrolledProps<T> */, MosaicState<T>> {
//    open var state: MosaicState<T>
//    open fun render(): Element
//    open var getRoot: Any
//    open var updateRoot: Any
//    open var replaceRoot: Any
//    open var actions: Any
//    open var childContext: Any
//    open var renderTree: Any
//    open var validateTree: Any
//
//    companion object {
//        var defaultProps: `T$0`
//        fun getDerivedStateFromProps(nextProps: Readonly<dynamic /* MosaicControlledProps<dynamic /* String | Number */> | MosaicUncontrolledProps<dynamic /* String | Number */> */>, prevState: MosaicState<dynamic /* String | Number */>): MosaicStatePartial<dynamic /* String | Number */>?
//    }
//}

//external open class Mosaic<T> : React.PureComponent<dynamic /* MosaicControlledProps<T> | MosaicUncontrolledProps<T> */> {
//    open fun render(): Element
//}

abstract external class Mosaic<T> : Component<MosaicControlledProps<T>, State>