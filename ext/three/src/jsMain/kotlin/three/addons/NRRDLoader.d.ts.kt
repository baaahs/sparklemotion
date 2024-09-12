@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external open class NRRDLoader(manager: LoadingManager = definedExternally) : Loader__1<Volume> {
    override var manager: LoadingManager
    override var path: String
    open var fieldFunctions: Any?
    open fun parse(data: String): Volume
    open fun parseChars(array: Array<Number>, start: Number = definedExternally, end: Number = definedExternally): String
    open fun setPath(value: String): NRRDLoader /* this */
}