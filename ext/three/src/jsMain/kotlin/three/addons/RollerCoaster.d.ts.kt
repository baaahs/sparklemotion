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

external interface Curve {
    fun getPointAt(u: Number): Vector3
    fun getTangentAt(u: Number): Vector3
}

external open class RollerCoasterGeometry(curve: Curve, divisions: Number) : BufferGeometry__0

external open class RollerCoasterLiftersGeometry(curve: Curve, divisions: Number) : BufferGeometry__0

external open class RollerCoasterShadowGeometry(curve: Curve, divisions: Number) : BufferGeometry__0

external open class SkyGeometry : BufferGeometry__0

external open class TreesGeometry(landscape: Mesh__0) : BufferGeometry__0