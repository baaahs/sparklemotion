@file:JsModule("three")
@file:JsNonModule
package three.js

import kotlin.js.*
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

external interface ParseTrackNameResults {
    var nodeName: String
    var objectName: String
    var objectIndex: String
    var propertyName: String
    var propertyIndex: String
}

open external class Composite(targetGroup: Any, path: Any, parsedPath: Any = definedExternally) {
    open fun getValue(array: Any, offset: Number): Any
    open fun setValue(array: Any, offset: Number)
    open fun bind()
    open fun unbind()
}

open external class PropertyBinding(rootNode: Any, path: String, parsedPath: Any = definedExternally) {
    open var path: String
    open var parsedPath: Any
    open var node: Any
    open var rootNode: Any
    open fun getValue(targetArray: Any, offset: Number): Any
    open fun setValue(sourceArray: Any, offset: Number)
    open fun bind()
    open fun unbind()
    open var BindingType: `T$31`
    open var Versioning: `T$31`
    open var GetterByBindingType: Array<() -> Unit>
    open var SetterByBindingTypeAndVersioning: Array<Array<() -> Unit>>

    companion object {
        fun create(root: Any, path: Any, parsedPath: Any = definedExternally): dynamic /* PropertyBinding | Composite */
        fun sanitizeNodeName(name: String): String
        fun parseTrackName(trackName: String): ParseTrackNameResults
        fun findNode(root: Any, nodeName: String): Any
    }
}