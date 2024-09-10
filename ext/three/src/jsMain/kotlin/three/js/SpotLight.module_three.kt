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

open external class SpotLight : Light<SpotLightShadow> {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally, decay: Number = definedExternally)
    constructor()
    constructor(color: Color = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally)
    constructor(color: Color = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally, decay: Number = definedExternally)
    constructor(color: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally, distance: Number = definedExternally, angle: Number = definedExternally, penumbra: Number = definedExternally)
    open val isSpotLight: Boolean
    override var override: Any
    override val type: String /* String | "SpotLight" */
    override val position: Vector3
    open var target: Object3D__0
    override var castShadow: Boolean
    override var intensity: Number
    open var distance: Number
    open var angle: Number
    open var decay: Number
    override var shadow: SpotLightShadow
    open var power: Number
    open var penumbra: Number
    open var map: Texture?
}