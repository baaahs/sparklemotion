package baaahs

import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.GlslRenderer
import baaahs.gl.render.RenderSurface

class SurfaceManager(
    private val glslRenderer: GlslRenderer
) {
    private val changedSurfaces = mutableListOf<ShowRunner.SurfacesChanges>()
    internal val renderSurfaces: MutableMap<Surface, RenderSurface> = hashMapOf()
    private var totalSurfaceReceivers = 0

    fun getRenderSurfaces_ForTestOnly(): Map<Surface, RenderSurface> {
        return renderSurfaces
    }

    fun surfacesChanged(addedSurfaces: Collection<ShowRunner.SurfaceReceiver>, removedSurfaces: Collection<ShowRunner.SurfaceReceiver>) {
        changedSurfaces.add(ShowRunner.SurfacesChanges(ArrayList(addedSurfaces), ArrayList(removedSurfaces)))
    }

    fun requiresRemap(): Boolean {
        var anyChanges = false

        for ((added, removed) in changedSurfaces) {
            ShowRunner.logger.info { "ShowRunner surfaces changed! ${added.size} added, ${removed.size} removed" }
            for (receiver in removed) removeReceiver(receiver)
            for (receiver in added) addReceiver(receiver)
            anyChanges = true
        }
        changedSurfaces.clear()
        return anyChanges
    }

    fun remap(renderPlan: RenderPlan) {
        renderSurfaces.forEach { (surface, renderSurface) ->
            renderPlan.programs.forEach { (patch: LinkedPatch, program: GlslProgram) ->
                if (patch.matches(surface)) {
                    renderSurface.useProgram(program)
                }
            }
        }
    }

    fun clearRenderPlans() {
        renderSurfaces.values.forEach { it.release() }
    }

    fun getSurfaceCount(): Int {
        return renderSurfaces.size
    }

    fun sendFrame() {
        renderSurfaces.values.forEach { renderSurface ->
//            if (shaderBuffers.size != 1) {
//                throw IllegalStateException("Too many shader buffers for ${surface.describe()}: $shaderBuffers")
//            }

            renderSurface.receivers.forEach { receiver ->
                // TODO: The send might return an error, at which point this receiver should be nuked
                // from the list of receivers for this surface. I'm not quite sure the best way to do
                // that so I'm leaving this note.
                receiver.send(renderSurface.pixels)
            }
        }
    }

    private fun addReceiver(receiver: ShowRunner.SurfaceReceiver) {
        val surface = receiver.surface
        val renderSurface = renderSurfaces.getOrPut(surface) {
            glslRenderer.addSurface(surface)
        }
        renderSurface.receivers.add(receiver)

        totalSurfaceReceivers++
    }

    private fun removeReceiver(receiver: ShowRunner.SurfaceReceiver) {
        val surface = receiver.surface
        val renderSurface = renderSurfaces.get(surface)
            ?: throw IllegalStateException("huh? no SurfaceBinder for $surface")

        if (!renderSurface.receivers.remove(receiver)) {
            throw IllegalStateException("huh? receiver not registered for $surface")
        }

        if (renderSurface.receivers.isEmpty()) {
            glslRenderer.removeSurface(renderSurface)
            renderSurface.release()
            renderSurfaces.remove(surface)
        }

        totalSurfaceReceivers--
    }
}