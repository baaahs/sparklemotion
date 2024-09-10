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