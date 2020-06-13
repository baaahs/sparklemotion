@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package external.mosaic

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

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