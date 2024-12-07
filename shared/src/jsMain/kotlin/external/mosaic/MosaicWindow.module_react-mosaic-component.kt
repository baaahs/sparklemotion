@file:JsModule("react-mosaic-component")
package external.mosaic

import react.ElementType
import react.Props
import react.ReactElement

external interface MosaicWindowProps<T>: Props {
    var title: String
    var path: Array<String /* 'first' | 'second' */>
    var className: String?
        get() = definedExternally
        set(value) = definedExternally
    var toolbarControls: Any?
        get() = definedExternally
        set(value) = definedExternally
    var additionalControls: Any?
        get() = definedExternally
        set(value) = definedExternally
    var additionalControlButtonText: String?
        get() = definedExternally
        set(value) = definedExternally
    var draggable: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var createNode: CreateNode<T>?
        get() = definedExternally
        set(value) = definedExternally
    var renderPreview: ((props: MosaicWindowProps<T>) -> ReactElement<*>)?
        get() = definedExternally
        set(value) = definedExternally
    var renderToolbar: ((props: MosaicWindowProps<T>, draggable: Boolean?) -> ReactElement<*>)?
        get() = definedExternally
        set(value) = definedExternally
    var onDragStart: (() -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onDragEnd: ((type: String /* 'drop' | 'reset' */) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

//abstract external class MosaicWindow<T> : Component<MosaicWindowProps<T>, State>

external val MosaicWindow: ElementType<MosaicWindowProps<*>>