package baaahs.app.ui.patchmod

import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.ProgramNode
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.OpenShader
import baaahs.plugin.core.datasource.XyPadDataSource
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.Stream
import baaahs.show.live.GadgetProvider
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.ui.View

// TODO: This should come from plugins.
val patchModBuilders = listOf(
    PositionAndScalePatchMod,
    RotationPatchMod,
    ColorPatchMod
)

interface PatchMod {
    val title: String
    val dataSources: List<DataSource>

    fun getView(openPatch: OpenPatch): View
    fun maybeWrapLink(inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode?
    fun registerGadgets(gadgetProvider: GadgetProvider)
}

interface PatchModBuilder {
    fun buildIfRelevant(
        openShader: OpenShader,
        patchId: String,
        toolchain: Toolchain,
        gadgetProvider: GadgetProvider
    ): PatchMod? = null
}

class PositionAndScalePatchMod(
    val patchId: String,
    val uvInputPort: InputPort,
    val toolchain: Toolchain
) : PatchMod {
    override val title: String
        get() = "Position/Scale"

    val positionXyPad = XyPad(
        "$patchId ${uvInputPort.id} offset",
        Vector2F.origin,
        Vector2F(-.75f, -.75f),
        Vector2F(.75f, .75f)
    )

    private val positionDataSource = XyPadDataSource(
        "$patchId ${uvInputPort.id} offset",
        Vector2F.origin,
        Vector2F(-.75f, -.75f),
        Vector2F(.75f, .75f)
    )

    override val dataSources: List<DataSource>
        get() = listOf(positionDataSource)

    override fun getView(openPatch: OpenPatch): View =
        patchModViews.forPositionAndScale(this, openPatch)

    override fun maybeWrapLink(inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode? {
        return if (inputPortId == uvInputPort.id) {
            val openShader = toolchain.openShader(
                Shader(
                    "Position and Scale Patch Mod for ${forShader.title}",
                    /** language=glsl */
                    """
                        // @param uvIn uv-coordinate
                        // @param offset xy-coordinate
                        // @return uv-coordinate
                        vec2 main(vec2 uvIn, vec2 offset) {
                            return uvIn - offset;
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
                    )
                ),
                Stream.Main,
                0f
            )
        } else null
    }

    override fun registerGadgets(gadgetProvider: GadgetProvider) {
        gadgetProvider.registerGadget(positionDataSource.suggestId(), this.positionXyPad, positionDataSource)
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

class RotationPatchMod : PatchMod {
    override val title: String
        get() = "Rotation"

    override val dataSources: List<DataSource>
        get() = emptyList()

    override fun getView(openPatch: OpenPatch): View =
        patchModViews.forRotation(this, openPatch)

    override fun maybeWrapLink(inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode? {
        return null
    }

    override fun registerGadgets(gadgetProvider: GadgetProvider) {}

    companion object : PatchModBuilder {}
}

class ColorPatchMod : PatchMod {
    override val title: String
        get() = "Color"

    override val dataSources: List<DataSource>
        get() = emptyList()

    override fun getView(openPatch: OpenPatch): View =
        patchModViews.forColor(this, openPatch)

    override fun maybeWrapLink(inputPortId: String, link: ProgramNode, forShader: OpenShader): ProgramNode? {
        return null
    }

    override fun registerGadgets(gadgetProvider: GadgetProvider) {}

    companion object : PatchModBuilder {}
}

interface PatchModViews {
    fun forPositionAndScale(patchMod: PositionAndScalePatchMod, openPatch: OpenPatch): View
    fun forRotation(patchMod: RotationPatchMod, openPatch: OpenPatch): View
    fun forColor(patchMod: ColorPatchMod, openPatch: OpenPatch): View
}

val patchModViews by lazy { getPatchModViews() }
expect fun getPatchModViews(): PatchModViews