package three.addons

import three.IUniform__0

external interface `T$138` {
    var tDiffuse: IUniform__0
    var toneMappingExposure: IUniform__0
}

external object OutputShader {
    var name: String
    var uniforms: `T$138`
    var vertexShader: String
    var fragmentShader: String
}