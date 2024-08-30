package baaahs.app.ui.patchmod

import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.core.feed.SliderFeed
import baaahs.plugin.core.feed.XyPadFeed
import baaahs.show.Feed
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

    private val positionFeed = XyPadFeed(
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

    private val scaleFeed = SliderFeed(
        "$patchId ${uvInputPort.id} scale",
        1f,
        .1f,
        4f
    )
    val scaleSlider = scaleFeed.createGadget("Scale")

    override val feeds: List<Feed>
        get() = listOf(positionFeed, scaleFeed)

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
                            vec2 offsetAdjusted = vec2(offset.x, 0. - offset.y);
                            return (uvIn - .5) / scale - -offsetAdjusted + .5;
                        }
                    """.trimIndent(),
                )
            )
            LinkedPatch(
                openShader,
                mapOf(
                    "uvIn" to link,
                    "offset" to OpenPatch.FeedLink(
                        positionFeed, positionFeed.suggestId(),
                        emptyMap()
                    ),
                    "scale" to OpenPatch.FeedLink(
                        scaleFeed, scaleFeed.suggestId(),
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
        gadgetProvider.registerGadget(positionFeed.suggestId(), positionXyPad, positionFeed)
        gadgetProvider.registerGadget(scaleFeed.suggestId(), scaleSlider, scaleFeed)
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