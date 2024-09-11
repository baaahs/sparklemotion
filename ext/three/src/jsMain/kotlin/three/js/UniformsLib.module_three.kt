@file:JsModule("three")
@file:JsNonModule
package three.js

external interface IUniform<TValue> {
    var value: TValue
}

external interface IUniform__0 : IUniform<Any>

external interface `T$32` {
    var diffuse: IUniform<Color>
    var opacity: IUniform<Number>
    var map: IUniform<Any>
    var mapTransform: IUniform<Matrix3>
    var alphaMap: IUniform<Any>
    var alphaMapTransform: IUniform<Matrix3>
    var alphaTest: IUniform<Number>
}

external interface `T$33` {
    var specularMap: IUniform<Any>
    var specularMapTransform: IUniform<Matrix3>
}

external interface `T$34` {
    var envMap: IUniform<Any>
    var envMapRotation: IUniform<Matrix3>
    var flipEnvMap: IUniform<Number>
    var reflectivity: IUniform<Number>
    var ior: IUniform<Number>
    var refractRatio: IUniform<Number>
}

external interface `T$35` {
    var aoMap: IUniform<Any>
    var aoMapIntensity: IUniform<Number>
    var aoMapTransform: IUniform<Matrix3>
}

external interface `T$36` {
    var lightMap: IUniform<Number>
    var lightMapIntensity: IUniform<Number>
    var lightMapTransform: IUniform<Matrix3>
}

external interface `T$37` {
    var bumpMap: IUniform<Any>
    var bumpMapTransform: IUniform<Matrix3>
    var bumpScale: IUniform<Number>
}

external interface `T$38` {
    var normalMap: IUniform<Any>
    var normalMapTransform: IUniform<Matrix3>
    var normalScale: IUniform<Vector2>
}

external interface `T$39` {
    var displacementMap: IUniform<Any>
    var displacementMapTransform: IUniform<Matrix3>
    var displacementScale: IUniform<Number>
    var displacementBias: IUniform<Number>
}

external interface `T$40` {
    var emissiveMap: IUniform<Any>
    var emissiveMapTransform: IUniform<Matrix3>
}

external interface `T$41` {
    var metalnessMap: IUniform<Any>
    var metalnessMapTransform: IUniform<Matrix3>
}

external interface `T$42` {
    var roughnessMap: IUniform<Any>
    var roughnessMapTransform: IUniform<Matrix3>
}

external interface `T$43` {
    var gradientMap: IUniform<Any>
}

external interface `T$44` {
    var fogDensity: IUniform<Number>
    var fogNear: IUniform<Number>
    var fogFar: IUniform<Number>
    var fogColor: IUniform<Color>
}

external interface `T$45` {
    var direction: Any
    var color: Any
}

external interface `T$46` {
    var value: Array<Any>
    var properties: `T$45`
}

external interface `T$47` {
    var shadowIntensity: Number
    var shadowBias: Any
    var shadowNormalBias: Any
    var shadowRadius: Any
    var shadowMapSize: Any
}

external interface `T$48` {
    var value: Array<Any>
    var properties: `T$47`
}

external interface `T$49` {
    var color: Any
    var position: Any
    var direction: Any
    var distance: Any
    var coneCos: Any
    var penumbraCos: Any
    var decay: Any
}

external interface `T$50` {
    var value: Array<Any>
    var properties: `T$49`
}

external interface `T$51` {
    var value: Array<Any>
    var properties: `T$47`
}

external interface `T$52` {
    var color: Any
    var position: Any
    var decay: Any
    var distance: Any
}

external interface `T$53` {
    var value: Array<Any>
    var properties: `T$52`
}

external interface `T$54` {
    var shadowIntensity: Number
    var shadowBias: Any
    var shadowNormalBias: Any
    var shadowRadius: Any
    var shadowMapSize: Any
    var shadowCameraNear: Any
    var shadowCameraFar: Any
}

external interface `T$55` {
    var value: Array<Any>
    var properties: `T$54`
}

external interface `T$56` {
    var direction: Any
    var skycolor: Any
    var groundColor: Any
}

external interface `T$57` {
    var value: Array<Any>
    var properties: `T$56`
}

external interface `T$58` {
    var color: Any
    var position: Any
    var width: Any
    var height: Any
}

external interface `T$59` {
    var value: Array<Any>
    var properties: `T$58`
}

external interface `T$60` {
    var ambientLightColor: IUniform<Array<Any>>
    var lightProbe: IUniform<Array<Any>>
    var directionalLights: `T$46`
    var directionalLightShadows: `T$48`
    var directionalShadowMap: IUniform<Array<Any>>
    var directionalShadowMatrix: IUniform<Array<Any>>
    var spotLights: `T$50`
    var spotLightShadows: `T$51`
    var spotLightMap: IUniform<Array<Any>>
    var spotShadowMap: IUniform<Array<Any>>
    var spotLightMatrix: IUniform<Array<Any>>
    var pointLights: `T$53`
    var pointLightShadows: `T$55`
    var pointShadowMap: IUniform<Array<Any>>
    var pointShadowMatrix: IUniform<Array<Any>>
    var hemisphereLights: `T$57`
    var rectAreaLights: `T$59`
    var ltc_1: IUniform<Any>
    var ltc_2: IUniform<Any>
}

external interface `T$61` {
    var diffuse: IUniform<Color>
    var opacity: IUniform<Number>
    var size: IUniform<Number>
    var scale: IUniform<Number>
    var map: IUniform<Any>
    var alphaMap: IUniform<Any>
    var alphaTest: IUniform<Number>
    var uvTransform: IUniform<Matrix3>
}

external interface `T$62` {
    var diffuse: IUniform<Color>
    var opacity: IUniform<Number>
    var center: IUniform<Vector2>
    var rotation: IUniform<Number>
    var map: IUniform<Any>
    var mapTransform: IUniform<Matrix3>
    var alphaMap: IUniform<Any>
    var alphaTest: IUniform<Number>
}

external object UniformsLib {
    var common: `T$32`
    var specularmap: `T$33`
    var envmap: `T$34`
    var aomap: `T$35`
    var lightmap: `T$36`
    var bumpmap: `T$37`
    var normalmap: `T$38`
    var displacementmap: `T$39`
    var emissivemap: `T$40`
    var metalnessmap: `T$41`
    var roughnessmap: `T$42`
    var gradientmap: `T$43`
    var fog: `T$44`
    var lights: `T$60`
    var points: `T$61`
    var sprite: `T$62`
}