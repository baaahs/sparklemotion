package three.addons

import js.objects.Record
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

open external class PLYLoader(manager: LoadingManager = definedExternally) : Loader__1<BufferGeometry<NormalOrGLBufferAttributes>> {
    open var propertyNameMapping: Any?
    open var customPropertyMapping: Record<String, Any>
    open fun setPropertyNameMapping(mapping: Any?)
    open fun setCustomPropertyNameMapping(mapping: Record<String, Any>)
    open fun parse(data: ArrayBuffer): BufferGeometry<NormalOrGLBufferAttributes>
    open fun parse(data: String): BufferGeometry<NormalOrGLBufferAttributes>
}