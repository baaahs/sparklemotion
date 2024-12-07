package three.addons

import three.IUniform
import three.Matrix4
import three.Texture
import three.Vector2

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

external object SSRShader {
    var name: String
    var defines: `T$149`
    var uniforms: `T$150`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$151` {
    var tDepth: IUniform<Texture?>
    var cameraNear: IUniform<Number>
    var cameraFar: IUniform<Number>
}

external object SSRDepthShader {
    var name: String
    var defines: `T$126`
    var uniforms: `T$151`
    var vertexShader: String
    var fragmentShader: String
}

external interface `T$152` {
    var tDiffuse: IUniform<Texture?>
    var resolution: IUniform<Vector2>
    var opacity: IUniform<Number>
}

external object SSRBlurShader {
    var name: String
    var uniforms: `T$152`
    var vertexShader: String
    var fragmentShader: String
}