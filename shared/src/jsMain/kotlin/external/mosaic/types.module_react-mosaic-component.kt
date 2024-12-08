package external.mosaic

import react.ReactElement

external interface MosaicParent<T> {
    var direction: String /* 'row' | 'column' */
    var first: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
    var second: dynamic /* MosaicParent<T> | T */
        get() = definedExternally
        set(value) = definedExternally
    var splitPercentage: Number?
        get() = definedExternally
        set(value) = definedExternally
}

typealias MosaicPath = Array<String /* 'first' | 'second' */>

external interface MosaicUpdate<T> {
    var path: MosaicPath
    var spec: dynamic /* typealias MosaicUpdateSpec = dynamic */
        get() = definedExternally
        set(value) = definedExternally
}


typealias CreateNode<T> = (args: Array<Any>) -> dynamic

external interface EnabledResizeOptions {
    var minimumPaneSizePercentage: Number?
        get() = definedExternally
        set(value) = definedExternally
}

typealias TileRenderer<T> = (t: T, path: Array<String /* 'first' | 'second' */>) -> ReactElement<*>
