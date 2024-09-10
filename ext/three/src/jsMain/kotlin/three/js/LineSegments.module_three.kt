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

open external class LineSegments<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Line<TGeometry, TMaterial, TEventMap> {
    open val isLineSegments: Boolean
    override var override: Any
    override val type: String /* String | "LineSegments" */
}

typealias LineSegments__0 = LineSegments<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>

open external class LineSegments__2<TGeometry : BufferGeometry__0, TMaterial> : LineSegments<TGeometry, TMaterial, Object3DEventMap>
