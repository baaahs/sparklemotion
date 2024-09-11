@file:JsModule("three")
@file:JsNonModule
package three.js

open external class PointLight : Light<PointLightShadow> {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor()
    constructor(color: Color = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally)
    open val isPointLight: Boolean
    override var type: String
    override var intensity: Number
    open var distance: Number
    override var castShadow: Boolean
    open var decay: Number
    override var shadow: PointLightShadow
    open var power: Number
}