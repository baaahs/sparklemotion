@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Color

open external class ClearPass : Pass {
    constructor(clearColor: Color = definedExternally, clearAlpha: Number = definedExternally)
    constructor()
    constructor(clearColor: Color = definedExternally)
    constructor(clearColor: String = definedExternally, clearAlpha: Number = definedExternally)
    constructor(clearColor: String = definedExternally)
    constructor(clearColor: Number = definedExternally, clearAlpha: Number = definedExternally)
    constructor(clearColor: Number = definedExternally)
    open var clearColor: dynamic /* Color | String | Number */
    open var clearAlpha: Number
}