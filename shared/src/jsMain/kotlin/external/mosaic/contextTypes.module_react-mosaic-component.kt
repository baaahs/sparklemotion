package external.mosaic

import kotlin.js.Promise

external interface MosaicContext<T> {
    var mosaicActions: MosaicRootActions<T>
    var mosaicId: String
}

external interface MosaicWindowContext {
    var mosaicWindowActions: MosaicWindowActions
}

external interface MosaicRootActions<T> {
    var expand: (path: MosaicPath, percentage: Number) -> Unit
    var remove: (path: MosaicPath) -> Unit
    var hide: (path: MosaicPath) -> Unit
    var replaceWith: (path: MosaicPath, node: dynamic /* MosaicParent<T> | T */) -> Unit
    var updateTree: (updates: Array<MosaicUpdate<T>>, suppressOnRelease: Boolean) -> Unit
    var getRoot: () -> dynamic
}

external interface MosaicWindowActions {
    var split: (args: Array<Any>) -> Promise<Unit>
    var replaceWithNew: (args: Array<Any>) -> Promise<Unit>
    var setAdditionalControlsOpen: (open: Boolean) -> Unit
    var getPath: () -> MosaicPath
//    var connectDragSource: (connectedElements: React.ReactElement<Any>) -> React.ReactElement?
}