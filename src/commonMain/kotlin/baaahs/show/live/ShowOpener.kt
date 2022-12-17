package baaahs.show.live

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.getBang
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.shader.OpenShader
import baaahs.internalTimerClock
import baaahs.show.*
import baaahs.util.CacheBuilder
import baaahs.util.Logger
import baaahs.util.elapsedMs

open class ShowOpener(
    private val toolchain: Toolchain,
    private val show: Show,
    private val showPlayer: ShowPlayer
): OpenContext {
    private val startTime = internalTimerClock.now()

    init { logger.debug { "Opening show ${show.title}" } }

    private val implicitControls = mutableMapOf<String, Control>()

    private val openControlCache = CacheBuilder<String, OpenControl> { controlId ->
        (implicitControls[controlId] ?: show.getControl(controlId))
            .open(controlId, this, showPlayer)
    }

    override val allControls: List<OpenControl> get() = openControlCache.all.values.toList()

    private val openShaders = CacheBuilder<String, OpenShader> { shaderId ->
        openShader(show.shaders.getBang(shaderId, "shaders"))
    }

    private val resolver = PatchResolver(
        openShaders,
        show.patches,
        show.dataSources,
        toolchain,
        object : GadgetProvider {
            override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) =
                showPlayer.registerGadget(id, gadget)

            override fun <T : Gadget> useGadget(id: String): T =
                showPlayer.useGadget(id)
        }
    )

    private val allPatches = resolver.getResolvedPatches()
    override val allPatchModFeeds: List<Feed>
        get() = allPatches.values.flatMap { it.patchMods.flatMap { it.feeds } }

    override fun findControl(id: String): OpenControl? =
        if (implicitControls.contains(id) || show.controls.contains(id)) openControlCache[id] else null

    override fun getControl(id: String): OpenControl = openControlCache[id]

    override fun getDataSource(id: String): Feed =
        show.dataSources.getBang(id, "data source")

    override fun getPanel(id: String): Panel =
        show.layouts.panels.getBang(id, "panel")

    override fun getPatch(it: String): OpenPatch =
        allPatches.getBang(it, "patch")

    fun openShow(showState: ShowState? = null): OpenShow {
        implicitControls.putAll(show.findImplicitControls())

        val implicitOpenControls = implicitControls.keys.map { getControl(it) }

        return OpenShow(show, showPlayer, this, implicitOpenControls)
            .also { if (showState != null) it.applyState(showState) }
            .also { it.applyConstraints() }
            .also { logger.info { "Opened \"${show.title}\" in ${startTime.elapsedMs() }ms." } }
    }

    open fun openShader(shader: Shader) =
        toolchain.openShader(shader)

    override fun release() {
//        allControls.forEach { it.release() }
//        openShaders.forEach { it.release() }
//        allPatches.forEach { it.release() }
    }

    companion object {
        private val logger = Logger<ShowOpener>()
    }
}

interface GadgetProvider {
    fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed? = null)
    fun <T : Gadget> useGadget(id: String): T = error("override me?")
}