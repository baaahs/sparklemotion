@file:JsModule("three")
@file:JsNonModule
package three

open external class AmbientLight : Light<Nothing?> {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor()
    constructor(color: Color = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally)
    open val isAmbientLight: Boolean
    override var override: Any
    override val type: String /* String | "AmbientLight" */
}