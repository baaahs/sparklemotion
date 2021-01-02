@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

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

external interface ParseTrackNameResults {
    var nodeName: String
    var objectName: String
    var objectIndex: String
    var propertyName: String
    var propertyIndex: String
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
    open var BindingType: `T$20`
    open var Versioning: `T$20`
    open var GetterByBindingType: Array<Function<*>>
    open var SetterByBindingTypeAndVersioning: Array<Array<Function<*>>>
    open class Composite(targetGroup: Any, path: Any, parsedPath: Any = definedExternally) {
        open fun getValue(array: Any, offset: Number): Any
        open fun setValue(array: Any, offset: Number)
        open fun bind()
        open fun unbind()
    }

    companion object {
        fun create(root: Any, path: Any, parsedPath: Any = definedExternally): dynamic /* PropertyBinding | PropertyBinding.Composite */
        fun sanitizeNodeName(name: String): String
        fun parseTrackName(trackName: String): ParseTrackNameResults
        fun findNode(root: Any, nodeName: String): Any
    }
}