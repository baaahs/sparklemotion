package baaahs.app.ui.patchmod

import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.core.datasource.SliderDataSource
import baaahs.plugin.core.datasource.XyPadDataSource
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.live.GadgetProvider
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.ui.View

class PositionAndScalePatchMod(
    val patchId: String,
    val uvInputPort: InputPort,
    val toolchain: Toolchain
) : PatchMod {
    override val title: String
        get() = "Position/Scale"

    private val positionDataSource = XyPadDataSource(
        "$patchId ${uvInputPort.id} offset",
        Vector2F.origin,
        Vector2F(-.75f, -.75f),
        Vector2F(.75f, .75f)
    )
    val positionXyPad = XyPad(
        "$patchId ${uvInputPort.id} offset",
        Vector2F.origin,
        Vector2F(-.75f, -.75f),
        Vector2F(.75f, .75f)
    )

    private val scaleDataSource = SliderDataSource(
        "$patchId ${uvInputPort.id} scale",
        1f,
        .1f,
        4f
    )
    val scaleSlider = scaleDataSource.createGadget()

    override val dataSources: List<DataSource>
        get() = listOf(positionDataSource, scaleDataSource)

    override fun getView(openPatch: OpenPatch): View =
        patchModViews.forPositionAndScale(this, openPatch)

    override fun maybeWrapLink(moddedNode: LinkedPatch, inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode? {
        return if (inputPortId == uvInputPort.id) {
            val openShader = toolchain.openShader(
                Shader(
                    "Position and Scale patchmod for ${forShader.title}",
                    /** language=glsl */
                    /** language=glsl */
                    """
                        // @param uvIn uv-coordinate
                        // @param offset xy-coordinate
                        // @param scale float
                        // @return uv-coordinate
                        vec2 main(vec2 uvIn, vec2 offset, float scale) {
                            return (uvIn - offset - .5) / scale + .5;
                        }
                    """.trimIndent(),
                )
            )
            LinkedPatch(
                openShader,
                mapOf(
                    "uvIn" to link,
                    "offset" to OpenPatch.DataSourceLink(
                        positionDataSource, positionDataSource.suggestId(),
                        emptyMap()
                    ),
                    "scale" to OpenPatch.DataSourceLink(
                        scaleDataSource, scaleDataSource.suggestId(),
                        emptyMap()
                    )
                ),
                Stream.Main,
                0f,
                modsNode = moddedNode
            )
        } else null
    }

    override fun registerGadgets(gadgetProvider: GadgetProvider) {
        gadgetProvider.registerGadget(positionDataSource.suggestId(), positionXyPad, positionDataSource)
        gadgetProvider.registerGadget(scaleDataSource.suggestId(), scaleSlider, scaleDataSource)
    }

    companion object : PatchModBuilder {
        override fun buildIfRelevant(
            openShader: OpenShader,
            patchId: String,
            toolchain: Toolchain,
            gadgetProvider: GadgetProvider
        ): PatchMod? {
            val uvInputPort = openShader.inputPorts.firstOrNull() { it.contentType == ContentType.UvCoordinate }
                ?: return null
            return PositionAndScalePatchMod(patchId, uvInputPort, toolchain)
                .also { it.registerGadgets(gadgetProvider) }
        }
    }
}