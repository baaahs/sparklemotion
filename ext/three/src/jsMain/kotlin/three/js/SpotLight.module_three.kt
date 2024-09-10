package three.js

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
    open var target: Object3D/*<Object3DEventMap>*/
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