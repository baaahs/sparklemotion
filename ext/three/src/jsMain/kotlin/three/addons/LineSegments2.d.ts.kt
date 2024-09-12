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

external open class LineSegments2(geometry: LineSegmentsGeometry = definedExternally, material: LineMaterial = definedExternally) : Mesh__0 {
    override var geometry: LineSegmentsGeometry
    override var material: LineMaterial
    open val isLineSegments2: Boolean
    open fun computeLineDistances(): LineSegments2 /* this */
}