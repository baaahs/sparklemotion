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

external interface `T$162` {
    var u_size: IUniform__0
    var u_renderstyle: IUniform__0
    var u_renderthreshold: IUniform__0
    var u_clim: IUniform__0
    var u_data: IUniform__0
    var u_cmdata: IUniform__0
}

external object VolumeRenderShader1 {
    var uniforms: `T$162`
    var vertexShader: String
    var fragmentShader: String
}