package baaahs.gl

import baaahs.fixtures.DeviceType
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslCode
import baaahs.gl.glsl.ShaderAnalysis
import baaahs.gl.patch.*
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.Shader
import baaahs.show.ShaderChannel
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.MutableShow

interface Toolchain {
    val plugins: Plugins

    fun parse(src: String): GlslCode

    fun import(src: String): Shader

    fun analyze(shader: Shader): ShaderAnalysis

    fun analyze(src: String, shader: Shader? = null): ShaderAnalysis

    fun openShader(src: String): OpenShader

    fun openShader(shader: Shader): OpenShader

    fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader

    fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutableShaderInstance: MutableShaderInstance
    ): ShaderInstanceOptions

    fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch

    fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        deviceTypes: Collection<DeviceType> = emptyList()
    ): UnresolvedPatch
}

class RootToolchain(
    override val plugins: Plugins,
    val glslAnalyzer: GlslAnalyzer = GlslAnalyzer(plugins),
    val autoWirer: AutoWirer = AutoWirer(plugins)
) : Toolchain {
    override fun parse(src: String): GlslCode {
        return glslAnalyzer.parse(src)
    }

    override fun import(src: String): Shader {
        return glslAnalyzer.import(src)
    }

    override fun analyze(shader: Shader): ShaderAnalysis {
        return glslAnalyzer.analyze(shader)
    }

    override fun analyze(src: String, shader: Shader?): ShaderAnalysis {
        return glslAnalyzer.analyze(src, shader)
    }

    override fun openShader(src: String): OpenShader {
        return glslAnalyzer.openShader(src)
    }

    override fun openShader(shader: Shader): OpenShader {
        return glslAnalyzer.openShader(shader)
    }

    override fun openShader(shaderAnalysis: ShaderAnalysis): OpenShader {
        return glslAnalyzer.openShader(shaderAnalysis)
    }

    override fun wiringOptions(
        currentOpenShader: OpenShader,
        parentMutableShow: MutableShow,
        mutableShaderInstance: MutableShaderInstance
    ): ShaderInstanceOptions {
        val channelsInfo = ChannelsInfo(parentMutableShow, emptyList(), this)
        return ShaderInstanceOptions(
            currentOpenShader,
            ShaderChannel.Main,
            channelsInfo,
            currentLinks = mutableShaderInstance.incomingLinks,
            plugins = plugins
        )
    }

    override fun autoWire(
        vararg shaders: Shader,
        defaultPorts: Map<ContentType, MutablePort>,
        shaderChannel: ShaderChannel,
        deviceTypes: Collection<DeviceType>
    ): UnresolvedPatch {
        val openShaders = shaders.associate { it to glslAnalyzer.openShader(it) }
        return autoWirer.autoWire(openShaders.values, shaderChannel, defaultPorts, deviceTypes)
    }

    override fun autoWire(
        vararg shaders: OpenShader,
        defaultPorts: Map<ContentType, MutablePort>,
        shaderChannel: ShaderChannel,
        deviceTypes: Collection<DeviceType>
    ): UnresolvedPatch {
        return autoWirer.autoWire(shaders.toList(), shaderChannel, defaultPorts, deviceTypes)
    }
}