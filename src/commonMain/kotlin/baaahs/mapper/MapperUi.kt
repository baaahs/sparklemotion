package baaahs.mapper

import baaahs.MediaDevices
import baaahs.imaging.Bitmap
import baaahs.imaging.Image
import baaahs.model.Model

interface MapperUi {
    var message: String?
    var message2: String?

    fun addWireframe(model: Model)
    fun showLiveCamImage(image: Image, changeRegion: MediaDevices.Region? = null)
    fun showSnapshot(bitmap: Bitmap)
    fun showBaseImage(bitmap: Bitmap)
    fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region? = null)
    fun showPanelMask(bitmap: Bitmap, changeRegion: MediaDevices.Region? = null)
    fun showMessage(message: String)
    fun showMessage2(message: String)
    fun setRedo(fn: (suspend () -> Unit)?)
    fun lockUi(): CameraPosition
    fun unlockUi()
    fun getAllSurfaceVisualizers(): List<Mapper.EntityDepiction>
    fun getVisibleSurfaces(): List<Mapper.VisibleSurface>
    fun showCandidates(orderedPanels: List<Pair<Mapper.VisibleSurface, Float>>)
    fun intersectingSurface(uv: Uv, visibleSurfaces: List<Mapper.VisibleSurface>): Mapper.VisibleSurface?
    fun showStats(total: Int, mapped: Int, visible: Int)
    fun close()
    fun addExistingSession(name: String)
    fun pauseForUserInteraction()
}