package baaahs.gl.patch

import baaahs.device.FixtureType
import baaahs.gl.shader.OpenShader
import baaahs.plugin.Plugins
import baaahs.show.ShaderChannel
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
    ): UnresolvedPatches {
        val siblingPatches = shaders.map {
            OpenPatch(it, emptyMap(), shaderChannel, 0f)
        }

        val parentShow = null as OpenShow? // TODO: test with non-null?
        val channelsInfo = ChannelsInfo(
            parentShow, if (fixtureTypes.isEmpty()) plugins.fixtureTypes.all else fixtureTypes
        )

        // First pass: gather shader output ports.
        val unresolvedPatches =
            shaders.associateWith { openShader ->
                autoWire(openShader, shaderChannel, channelsInfo, defaultPorts)
            }

        return UnresolvedPatches(unresolvedPatches.values.toList())
    }

    fun autoWire(
        openShader: OpenShader,
        shaderChannel: ShaderChannel = ShaderChannel.Main,
        defaultPorts: Map<ContentType, MutablePort> = emptyMap(),
        fixtureTypes: Collection<FixtureType> = emptyList()
    ): UnresolvedPatch {
        val parentShow = null as OpenShow? // TODO: test with non-null?
        val channelsInfo = ChannelsInfo(
            parentShow, if (fixtureTypes.isEmpty()) plugins.fixtureTypes.all else fixtureTypes
        )

        return autoWire(openShader, shaderChannel, channelsInfo, defaultPorts)
    }

    private fun autoWire(
        openShader: OpenShader,
        shaderChannel: ShaderChannel,
        channelsInfo: ChannelsInfo,
        defaultPorts: Map<ContentType, MutablePort>
    ): UnresolvedPatch {
        val patchOptions =
            PatchOptions(openShader, shaderChannel, channelsInfo, defaultPorts, emptyMap(), plugins)
        return UnresolvedPatch(
            MutableShader(openShader.shader),
            openShader.inputPorts.associateWith { inputPort ->
                patchOptions.inputPortLinkOptions[inputPort.id]?.toMutableList() ?: mutableListOf()
            },
            shaderChannel,
            0f
        )
    }
}