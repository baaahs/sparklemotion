@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Loader__1
import three.LoadingManager

open external class NRRDLoader(manager: LoadingManager = definedExternally) : Loader__1<Volume> {
    override var manager: LoadingManager
    override var path: String
    open var fieldFunctions: Any?
    open fun parse(data: String): Volume
    open fun parseChars(array: Array<Number>, start: Number = definedExternally, end: Number = definedExternally): String
    override fun setPath(value: String): NRRDLoader /* this */
}