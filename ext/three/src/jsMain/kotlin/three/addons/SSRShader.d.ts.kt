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

external interface `T$149` {
    var MAX_STEP: Number
    var isPerspectiveCamera: Boolean
    var isDistanceAttenuation: Boolean
    var isFresnel: Boolean
    var isInfiniteThick: Boolean
    var isSelective: Boolean
}

external interface `T$150` {
    var tDiffuse: IUniform<Texture?>
    var tNormal: IUniform<Texture?>
    var tMetalness: IUniform<Texture?>
    var tDepth: IUniform<Texture?>
    var cameraNear: IUniform<Number>
    var cameraFar: IUniform<Number>
    var resolution: IUniform<Vector2>
    var cameraProjectionMatrix: IUniform<Matrix4>
    var cameraInverseProjectionMatrix: IUniform<Matrix4>
    var opacity: IUniform<Number>
    var maxDistance: IUniform<Number>
    var cameraRange: IUniform<Number>
    var thickness: IUniform<Number>
}

@Suppress("EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface SSRShader {
    var name: String
    var defines: `T$149`
    var uniforms: `T$150`
    var vertexShader: String
    var fragmentShader: String

    companion object : SSRShader by definedExternally
}

external interface `T$151` {
    var tDepth: IUniform<Texture?>
    var cameraNear: IUniform<Number>
    var cameraFar: IUniform<Number>
}

@Suppress("EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface SSRDepthShader {
    var name: String
    var defines: `T$126`
    var uniforms: `T$151`
    var vertexShader: String
    var fragmentShader: String

    companion object : SSRDepthShader by definedExternally
}

external interface `T$152` {
    var tDiffuse: IUniform<Texture?>
    var resolution: IUniform<Vector2>
    var opacity: IUniform<Number>
}

@Suppress("EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface SSRBlurShader {
    var name: String
    var uniforms: `T$152`
    var vertexShader: String
    var fragmentShader: String

    companion object : SSRBlurShader by definedExternally
}