package three.addons

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
import three.*
import kotlin.js.*

open external class SkyMesh : Mesh<BoxGeometry, /*Node*/Material> {
    open var turbidity: Any
    open var rayleigh: Any
    open var mieCoefficient: Any
    open var mieDirectionalG: Any
    open var sunPosition: Any
    open var upUniform: Any
    open val isSky: Boolean
}