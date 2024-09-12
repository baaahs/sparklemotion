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

external interface `T$124` {
    var PERSPECTIVE_CAMERA: Number
    var SAMPLES: Number
    var NORMAL_VECTOR_TYPE: Number
    var DEPTH_SWIZZLING: String
    var SCREEN_SPACE_RADIUS: Number
    var SCREEN_SPACE_RADIUS_SCALE: Number
    var SCENE_CLIP_BOX: Number
}

external interface `T$125` {
    var tNormal: IUniform__0
    var tDepth: IUniform__0
    var tNoise: IUniform__0
    var resolution: IUniform<Vector2>
    var cameraNear: IUniform__0
    var cameraFar: IUniform__0
    var cameraProjectionMatrix: IUniform<Matrix4>
    var cameraProjectionMatrixInverse: IUniform<Matrix4>
    var radius: IUniform<Number>
    var distanceExponent: IUniform<Number>
    var thickness: IUniform<Number>
    var distanceFallOff: IUniform<Number>
    var scale: IUniform<Number>
    var sceneBoxMin: IUniform<Vector3>
    var sceneBoxMax: IUniform<Vector3>
}

external object GTAOShader {
    var name: String
    var defines: `T$124`
    var uniforms: `T$125`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$126` {
    var PERSPECTIVE_CAMERA: Number
}

external interface `T$127` {
    var tDepth: IUniform__0
    var cameraNear: IUniform__0
    var cameraFar: IUniform__0
}

external object GTAODepthShader {
    var name: String
    var defines: `T$126`
    var uniforms: `T$127`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$128` {
    var tDiffuse: IUniform__0
    var intensity: IUniform<Number>
}

external object GTAOBlendShader {
    var name: String
    var uniforms: `T$128`
    var vertexShader: String
    var fragmentShader: String
}

external fun generateMagicSquareNoise(samples: Number = definedExternally): DataTexture