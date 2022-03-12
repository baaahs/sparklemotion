package baaahs.gl.patch

import baaahs.device.FixtureType
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
import baaahs.show.Surfaces
import baaahs.show.live.LiveShaderInstance
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutablePort
import baaahs.show.mutable.MutableShader

class AutoWirer(private val plugins: Plugins) {
    fun autoWire(
        shaders: Collection<OpenShader>,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        fixtureTypes: Collection<FixtureType> = emptyList()
    ): UnresolvedPatch {
        val siblingsPatch = OpenPatch(shaders.map {
            LiveShaderInstance(it, emptyMap(), shaderChannel, 0f)
        }, Surfaces.AllSurfaces)

        val parentShow = null as OpenShow? // TODO: test with non-null?
        val channelsInfo = ChannelsInfo(
            parentShow, if (fixtureTypes.isEmpty()) plugins.fixtureTypes.all else fixtureTypes
        )

        // First pass: gather shader output ports.
        val shaderInstances =
            shaders.associateWith { openShader ->
                val shaderInstanceOptions =
                    ShaderInstanceOptions(openShader, shaderChannel, channelsInfo, defaultPorts, emptyMap(), plugins)

                val unresolvedShaderInstance = UnresolvedShaderInstance(
                    MutableShader(openShader.shader),
                    openShader.inputPorts.associateWith { inputPort ->
                        shaderInstanceOptions.inputPortLinkOptions[inputPort.id]?.toMutableList() ?: mutableListOf()
                    },
                    shaderChannel,
                    0f
                )

                unresolvedShaderInstance
            }

        return UnresolvedPatch(shaderInstances.values.toList())
    }
}