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

open external class PCDLoader(manager: LoadingManager = definedExternally) : Loader__1<Points<BufferGeometry<NormalOrGLBufferAttributes>, PointsMaterial>> {
    open var littleEndian: Boolean
    open fun parse(data: ArrayBuffer): Points<BufferGeometry<NormalOrGLBufferAttributes>, PointsMaterial>
    open fun parse(data: String): Points<BufferGeometry<NormalOrGLBufferAttributes>, PointsMaterial>
}