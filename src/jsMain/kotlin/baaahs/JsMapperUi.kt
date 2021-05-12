package baaahs

import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.imaging.Bitmap
import baaahs.imaging.CanvasBitmap
import baaahs.imaging.Image
import baaahs.imaging.NativeBitmap
import baaahs.mapper.MapperAppView
import baaahs.mapper.MapperAppViewProps
import baaahs.model.Model
import baaahs.sim.HostedWebApp
import baaahs.ui.Observable
import baaahs.ui.value
import baaahs.util.Logger
import baaahs.visualizer.Rotator
import kotlinext.js.jsObject
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.RBuilder
import react.ReactElement
import react.createElement
import react.dom.br
import react.dom.i
import three.js.*
import three.js.Color
import three_ext.CameraControls
import three_ext.Matrix4
import three_ext.plus
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.math.roundToInt

class MemoizedJsMapperUi(mapperUi: JsMapperUi) {
    val changedCamera = mapperUi::changedCamera
    val clickedPlay = mapperUi::clickedPlay
    val clickedPause = mapperUi::clickedPause
    val clickedRedo = mapperUi::clickedRedo
    val clickedStop = mapperUi::clickedStop
    val clickedGoToSurface = mapperUi::clickedGoToSurface
    val onKeydown = { event: Event -> mapperUi.gotUiKeypress(event as KeyboardEvent) }
}

class MapperStatus : Observable() {
    var message: String? by notifyOnChange(null)
    var message2: String? by notifyOnChange(null)
    var stats: (RBuilder.() -> Unit)? by notifyOnChange(null)
    var orderedPanels: List<Pair<JsMapperUi.VisibleSurface, Float>> by notifyOnChange(emptyList())
}

class JsMapperUi(private val statusListener: StatusListener? = null) : Observable(), MapperUi, HostedWebApp {
    private lateinit var listener: MapperUi.Listener

    override var devices: List<MediaDevices.Device> by notifyOnChange(emptyList())
    var selectedDevice: MediaDevices.Device? by notifyOnChange(null)

    override fun listen(listener: MapperUi.Listener) {
        this.listener = listener
    }

    var width by notifyOnChange(512)
    var height by notifyOnChange(384)

    var uiWidth by notifyOnChange(512)
    var uiHeight by notifyOnChange(384)

    private var haveCamDimensions = false
    var camWidth by notifyOnChange(0)
    var camHeight by notifyOnChange(0)

    private val clock = Clock()

    // onscreen renderer for registration UI:
    private val uiRenderer = WebGLRenderer(jsObject { alpha = true })
    private val uiScene = Scene()
    private val uiCamera = PerspectiveCamera(45, width.toDouble() / height, 1, 10000).also { camera ->
        camera.position.z = 1000.0
        uiScene.add(camera)
    }
    private val uiControls: CameraControls = CameraControls(uiCamera, uiRenderer.domElement)
    val wireframe = Object3D()

    private val selectedSurfaces = mutableListOf<PanelInfo>()
    var uiLocked: Boolean by notifyOnChange(false)

    private lateinit var ui2dCanvas: HTMLCanvasElement
    private lateinit var ui2dCtx: CanvasRenderingContext2D

//    private val ui3dDiv = screen.first<HTMLCanvasElement>("mapperUi-3d-div")
    private val ui3dCanvas = uiRenderer.domElement

    private lateinit var diffCanvas: HTMLCanvasElement
    private lateinit var diffCtx: CanvasRenderingContext2D

    private lateinit var beforeCanvas: HTMLCanvasElement
    private lateinit var afterCanvas: HTMLCanvasElement

    val sessions = arrayListOf<String>()

    var pauseButtonEnabled by notifyOnChange(true)
    var playButtonEnabled by notifyOnChange(true)

    private val modelSurfaceInfos = mutableMapOf<Model.Surface, PanelInfo>()

    private var commandProgress = ""
    private var cameraZRotation = 0f

    val mapperStatus = MapperStatus()

    var redoFn: (() -> Unit)? by notifyOnChange(null)

    fun onMount(
        ui2dCanvas: HTMLCanvasElement,
        ui3dDiv: HTMLElement,
        diffCanvas: HTMLCanvasElement,
        beforeCanvas: HTMLCanvasElement,
        afterCanvas: HTMLCanvasElement
    ) {
        this.ui2dCanvas = ui2dCanvas
        this.ui2dCtx = ui2dCanvas.context2d()
        this.diffCanvas = diffCanvas
        this.diffCtx = diffCanvas.context2d()
        this.beforeCanvas = beforeCanvas
        this.afterCanvas = afterCanvas

        statusListener?.mapperStatusChanged(true)

        ui3dDiv.appendChild(ui3dCanvas)

//        screen.focus()
//        screen.addEventListener("keydown", { event -> gotUiKeypress(event as KeyboardEvent) })

        drawAnimationFrame()
    }

    fun onUnmount() {
    }

    fun gotUiKeypress(event: KeyboardEvent) {
        if (event.code == "Enter") {
            processCommand(commandProgress.trim())
            commandProgress = ""
        } else if (event.code == "Backspace") {
            if (commandProgress.isNotEmpty()) {
                commandProgress = commandProgress.substring(0..(commandProgress.length - 2))
            }
            checkProgress()
        } else if (commandProgress.isEmpty() && event.code == "KeyQ") {
            updateCameraRotation(if (event.shiftKey) 0.025f else 0.1f)
        } else if (commandProgress.isEmpty() && event.code == "KeyW") {
            updateCameraRotation(if (event.shiftKey) -0.025f else -0.1f)
        } else if (commandProgress.isEmpty() && event.code == "Digit0") {
            cameraZRotation = 0f
        } else if (event.key.length == 1) {
            commandProgress += event.key
            checkProgress()
        }
        showMessage2(commandProgress)
    }

    private fun checkProgress() {
        if (commandProgress.startsWith("/") && commandProgress.length > 1) {
            selectSurfacesMatching(commandProgress.substring(1))
        }
    }

    override fun addExistingSession(name: String) {
        sessions.add(name)
        notifyChanged()
    }

    private fun resetCameraRotation() {
        cameraZRotation = 0f
        updateCameraRotation(0f)
    }

    private fun updateCameraRotation(angle: Float) {
        cameraZRotation += angle
        uiCamera.up.set(0, 1, 0)

        val cameraAngle = Matrix4()
        val rotated = cameraAngle.makeRotationZ(cameraZRotation.toDouble())
        uiCamera.up.applyMatrix4(rotated)
    }

    private fun selectSurfacesMatching(pattern: String) {
        selectedSurfaces.forEach { it.deselect() }
        selectedSurfaces.clear()
        selectedSurfaces.addAll(modelSurfaceInfos.values.filter { it.name.contains(pattern, true) })
        selectedSurfaces.forEach { it.select() }
        notifyChanged()
    }

    private fun processCommand(command: String) {
        console.log(command)

        if (command.startsWith("g", ignoreCase = true) || command.startsWith("/")) {
            val surfaceName = command.substring(1).trim()
            goToSurface(surfaceName.toUpperCase())
        }
    }

    private fun drawAnimationFrame() {
        if (!uiLocked) {
            uiControls.update(clock.getDelta())
        }
        uiRenderer.render(uiScene, uiCamera)

        window.requestAnimationFrame { drawAnimationFrame() }
    }

    override fun render(): ReactElement {
        return createElement(MapperAppView, jsObject<MapperAppViewProps> {
            mapperUi = this@JsMapperUi
        })
    }

    private fun heightOrWindowHeight(parentNode: HTMLElement): Int {
        return if (parentNode.offsetHeight == 0) window.innerHeight else parentNode.offsetHeight
    }

    override fun onLaunch() {
        listener.onLaunch()
    }

    override fun onClose() {
        statusListener?.mapperStatusChanged(false)

        listener.onClose()
    }

    val diffCanvasScale = 1 / 3.0

    fun resizeTo(width: Int, height: Int) {
        if (width == width && height == height) return

        this.width = width
        this.height = height

        if (!haveCamDimensions) {
            camWidth = width
            camHeight = height
        }

        uiWidth = camWidth - 10
        uiHeight = camHeight - 10

        uiCamera.aspect = uiWidth.toDouble() / uiHeight
        uiCamera.updateProjectionMatrix()

        uiRenderer.setSize(uiWidth, uiHeight, true)
        notifyChanged()
    }

    override fun addWireframe(model: Model) {
        val vertices = model.geomVertices.map { v -> Vector3(v.x, v.y, v.z) }.toTypedArray()
        model.allSurfaces.forEach { surface ->
            val geom = Geometry()
            geom.vertices = vertices

            val faceNormalAcc = Vector3()
            val panelFaces = surface.faces.map { face ->
                val face3 = Face3(face.vertexA, face.vertexB, face.vertexC, Vector3(), Color(1, 1, 1))

                // just compute this face's normal
                geom.faces = arrayOf(face3)
                geom.computeFaceNormals()
                faceNormalAcc.add(face3.normal)

                face3
            }
            val surfaceNormal = faceNormalAcc.divideScalar(surface.faces.size.toDouble())

            val panelMaterial = MeshBasicMaterial().apply { color = Color(0, 0, 0) }
            val mesh = Mesh(geom, panelMaterial)
            mesh.asDynamic().name = surface.name
            uiScene.add(mesh)

            val lineMaterial = LineBasicMaterial().apply {
                color = Color(0f, 1f, 0f)
                linewidth = 2.0
            }

            // offset the wireframe by one of the panel's face normals so it's not clipped by the panel mesh
            surface.lines.forEach { line ->
                val lineGeom = BufferGeometry()
                lineGeom.setFromPoints(line.vertices.map { pt ->
                    Vector3(
                        pt.x,
                        pt.y,
                        pt.z
                    ) + surfaceNormal
                }.toTypedArray())
                wireframe.add(Line(lineGeom, lineMaterial))
            }

            geom.faces = panelFaces.toTypedArray()
            geom.computeFaceNormals()
            geom.computeVertexNormals()

            modelSurfaceInfos[surface] = PanelInfo(surface.name, panelFaces, mesh, geom, lineMaterial)
        }

        uiScene.add(wireframe)

        val originMarker = Mesh(
            SphereBufferGeometry(1, 32, 32),
            MeshBasicMaterial().apply { color = Color(0xff0000) })
        uiScene.add(originMarker)

        val boundingBox = Box3().setFromObject(wireframe)
        uiControls.fitToBox(boundingBox, false)
    }

    override fun lockUi(): MapperUi.CameraOrientation {
        uiLocked = true
        return CameraOrientation.from(uiCamera)
    }

    override fun unlockUi() {
        uiLocked = false
    }

    override fun getVisibleSurfaces(): List<MapperUi.VisibleSurface> {
        val visibleSurfaces = mutableListOf<MapperUi.VisibleSurface>()
        val screenBox = getScreenBox()
        val screenCenter = screenBox.center
        val cameraOrientation = CameraOrientation.from(uiCamera)

        modelSurfaceInfos.forEach { (panel, panelInfo) ->
            val panelPosition = panelInfo.geom.vertices[panelInfo.faces[0].a]
            val dirToCamera = uiCamera.position.clone().sub(panelPosition)
            dirToCamera.normalize()
            val angle = panelInfo.faces[0].normal.dot(dirToCamera)
            if (angle > 0) {
                panelInfo.mesh.updateMatrixWorld()

                val panelBoundingBox = panelInfo.boundingBox.project(uiCamera)
                val panelBoxOnScreen = calcBoundingBoxOnScreen(panelBoundingBox, screenCenter)
                panelInfo.boxOnScreen = panelBoxOnScreen
                if (panelBoxOnScreen.asDynamic().intersectsBox(screenBox)) {
                    val region = MediaDevices.Region(
                        panelBoxOnScreen.min.x.roundToInt(),
                        panelBoxOnScreen.min.y.roundToInt(),
                        panelBoxOnScreen.max.x.roundToInt(),
                        panelBoxOnScreen.max.y.roundToInt()
                    )
                    visibleSurfaces.add(VisibleSurface(panel, region, panelInfo, cameraOrientation))
                }
            }
        }

        return visibleSurfaces
    }

    inner class VisibleSurface(
        override val modelSurface: Model.Surface,
        override val boxOnScreen: MediaDevices.Region,
        val panelInfo: PanelInfo,
        cameraOrientation: CameraOrientation
    ) : MapperUi.VisibleSurface {
        private val camera = cameraOrientation.createCamera()
        private val geom = Geometry()
        private val material = PointsMaterial().apply {
            color = Color(0x00FF00)
            size = 5
        }
        private val points = Points(geom, material)
        private val pixels = mutableMapOf<Int, VisiblePixel>()

        override fun addPixel(pixelIndex: Int, x: Float, y: Float) {
            pixels[pixelIndex] = VisiblePixel(pixelIndex, x, y).apply { addToGeom() }
        }

        override fun translatePixelToPanelSpace(screenX: Float, screenY: Float): Vector2F? {
            val intersection = findIntersection(screenX, screenY) ?: return null

            val point = panelInfo.toPanelSpace(intersection.point.clone())
            console.log("   ---->", point.x, point.y, point.z)
            return Vector2F(point.x.toFloat(), point.y.toFloat())
        }

        override val pixelsInModelSpace: List<Vector3F?>
            get() {
                val vectors = mutableListOf<Vector3F?>()
                for (i in 0..(pixels.keys.maxOrNull()!!)) {
                    val position = pixels[i]?.positionInModel
                    vectors.add(position?.let {
                        Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat())
                    })
                }
                return vectors
            }

        private fun findIntersection(x: Float, y: Float): Intersection? {
            val raycaster = Raycaster()
            val pixelVector = Vector2(
                x / uiWidth * 2 - 1,
                -(y / uiHeight * 2 - 1)
            )
            raycaster.setFromCamera(pixelVector, camera)
            var intersections = raycaster.intersectObject(panelInfo.mesh, false)
            if (intersections.isEmpty()) {
                intersections = raycaster.intersectObject(uiScene, true)
                console.log("Couldn't find point in ${modelSurface.name}...", intersections)
            }
            if (intersections.isNotEmpty()) {
                return intersections.first()
            } else {
                return null
            }
        }

        override fun showPixels() {
            uiScene.add(points)
        }

        override fun hidePixels() {
            uiScene.remove(points)
        }

        inner class VisiblePixel(val pixelIndex: Int, val cameraX: Float, val cameraY: Float) {
            private val intersect: Intersection? by lazy { findIntersection(cameraX, cameraY) }
            val positionInModel = intersect?.point

            fun addToGeom() {
                if (intersect != null) {
                    // fill in any missing vertices...
                    while (geom.vertices.size < pixelIndex) {
                        geom.vertices[geom.vertices.size] = Vector3(0, 0, 0)
                    }

                    geom.vertices[pixelIndex] = intersect!!.point
                }
            }

            val panelSpaceCoords: Pair<Float, Float>? by lazy {
                if (positionInModel == null) {
                    null
                } else {
                    panelInfo.toPanelSpace(positionInModel)
                    positionInModel.x.toFloat() to positionInModel.y.toFloat()
                }
            }
        }
    }

    data class CameraOrientation(override val cameraMatrix: baaahs.geom.Matrix4, override val aspect: Double) :
        MapperUi.CameraOrientation {
        fun createCamera(): PerspectiveCamera {
            return PerspectiveCamera(45, aspect, 1, 10000).apply {
                matrix.fromArray(cameraMatrix.elements)
                // Get back position/rotation/scale attributes.
                matrix.asDynamic().decompose(position, quaternion, scale)
                updateMatrixWorld()
            }
        }

        companion object {
            fun from(camera: PerspectiveCamera): CameraOrientation {
                return CameraOrientation(
                    baaahs.geom.Matrix4(camera.matrix.toArray()),
                    camera.aspect.toDouble()
                )
            }
        }
    }

    override fun showCandidates(orderedPanels: List<Pair<MapperUi.VisibleSurface, Float>>) {
        orderedPanels as List<Pair<VisibleSurface, Float>>

        val firstGuess = orderedPanels.first()
        (firstGuess.first.panelInfo.mesh.material as MeshBasicMaterial).color.r += .25

        mapperStatus.orderedPanels = orderedPanels
    }

    override fun intersectingSurface(
        x: Int,
        y: Int,
        visibleSurfaces: List<MapperUi.VisibleSurface>
    ): MapperUi.VisibleSurface? {
        val raycaster = Raycaster()
        val pixelVector = Vector2(
            x.toFloat() / uiWidth * 2 - 1,
            -(y.toFloat() / uiHeight * 2 - 1)
        )
        raycaster.setFromCamera(pixelVector, uiCamera)
        val intersections = raycaster.intersectObject(uiScene, true)
        if (intersections.isNotEmpty()) {
            val intersect = intersections.first()
            return visibleSurfaces.find { it.modelSurface.name == intersect.`object`.name }
        } else {
            return null
        }
    }

    private fun getScreenBox(): Box2 {
        return Box2(Vector2(0, 0), Vector2(width, height))
    }

    private fun calcBoundingBoxOnScreen(box: Box3, screenCenter: Vector2): Box2 {
        val minX = ((box.min.x * screenCenter.x) + screenCenter.x).toInt()
        val maxX = ((box.max.x * screenCenter.x) + screenCenter.x).toInt()

        // Invert Y for screen coordinates.
        val minY = ((-box.max.y * screenCenter.y) + screenCenter.y).toInt()
        val maxY = ((-box.min.y * screenCenter.y) + screenCenter.y).toInt()

        return Box2(Vector2(minX, minY), Vector2(maxX, maxY))
    }

    override fun showCamImage(image: Image, changeRegion: MediaDevices.Region?) {
        if (!haveCamDimensions) {
            camWidth = image.width
            camHeight = image.height
            haveCamDimensions = true
            resizeTo(width, height)
        }

        ui2dCtx.resetTransform()
        CanvasBitmap(ui2dCanvas).drawImage(image)

        changeRegion?.apply {
            ui2dCtx.lineWidth = 2.0
            ui2dCtx.strokeStyle = "#ff0000"
            ui2dCtx.strokeRect(x0.toDouble(), y0.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region?) {
        diffCtx.resetTransform()
        diffCtx.scale(diffCanvasScale, diffCanvasScale)
        CanvasBitmap(diffCanvas).drawImage(deltaBitmap.asImage())

        changeRegion?.apply {
            diffCtx.strokeStyle = "#ff0000"
            diffCtx.lineWidth = 1 / diffCanvasScale
            diffCtx.strokeRect(
                x0.toDouble(), y0.toDouble(),
                changeRegion.width.toDouble(), changeRegion.height.toDouble()
            )
        }
    }

    override fun showMessage(message: String) {
        mapperStatus.message = message
        logger.info { "Message: $message" }
    }

    override fun showMessage2(message: String) {
        mapperStatus.message2 = message
    }

    override fun showBefore(bitmap: Bitmap) = showImage(bitmap, beforeCanvas)

    override fun showAfter(bitmap: Bitmap) = showImage(bitmap, afterCanvas)

    private fun showImage(bitmap: Bitmap, canvas: HTMLCanvasElement) {
        val beforeCtx = canvas.getContext("2d") as CanvasRenderingContext2D
        beforeCtx.resetTransform()
        beforeCtx.scale(.3, .3)
        val renderBitmap = when (bitmap) { // TODO: huh?
            is NativeBitmap -> bitmap.canvas
            is CanvasBitmap -> bitmap.canvas
            else -> bitmap as CanvasImageSource
        }
        beforeCtx.drawImage(renderBitmap, 0.0, 0.0)
    }

    override fun setRedo(fn: (suspend () -> Unit)?) {
//        if (fn == null) {
//            redoFn = null
//        } else {
//            redoFn = {
//                GlobalScope.launch { fn() }
//                redoButton.enabled(false)
//            }
//        }
//        redoButton.enabled(fn != null)
    }

    override fun showStats(total: Int, mapped: Int, visible: Int) {
        mapperStatus.stats = {
            i("fas fa-triangle") {
                +"Mapped: $mapped / $total"
                br {}
                +"Visible: $visible"
            }
        }
    }

    override fun pauseForUserInteraction() {
        clickedPause()
    }

    fun changedCamera(event: Event) {
        val selectedDeviceId = event.target?.value
        selectedDevice = devices.find { it.deviceId == selectedDeviceId}

        listener.useCamera(selectedDevice)
    }

    fun clickedPlay() {
        showPauseMode(false)
        listener.onStart()
    }

    fun clickedPause() {
        showPauseMode(true)
        listener.onPause()
    }

    fun clickedRedo() {
        redoFn?.invoke()
    }

    private fun showPauseMode(isPaused: Boolean) {
        pauseButtonEnabled = !isPaused
        playButtonEnabled = isPaused
        notifyChanged()
    }

    private fun HTMLButtonElement.enabled(isEnabled: Boolean) {
        style.opacity = if (isEnabled) "1" else ".5"
    }

    fun clickedStop() {
        listener.onStop()
    }

    fun clickedGoToSurface() {
        val surfaceName = window.prompt("Surface:")
        if (surfaceName != null && surfaceName.isNotEmpty()) {
            goToSurface(surfaceName.toUpperCase())
        }
    }

    private fun goToSurface(name: String) {
        val surface = modelSurfaceInfos.keys.find { it.name == name }
        if (surface != null) {
            val panelInfo = modelSurfaceInfos[surface]!!
            panelInfo.geom.computeBoundingBox()
            val surfaceCenter = panelInfo.center
            val surfaceNormal = panelInfo.surfaceNormal

            val newCamPosition = surfaceCenter.clone()
            newCamPosition.add(surfaceNormal.clone().multiplyScalar(100))
            resetCameraRotation()
            uiControls.setLookAt(
                newCamPosition.x, newCamPosition.y, newCamPosition.z,
                surfaceCenter.x, surfaceCenter.y, surfaceCenter.z, true
            )
        }
    }

    override fun close() {
    }

    interface StatusListener {
        fun mapperStatusChanged(isRunning: Boolean)
    }

    companion object {
        internal val logger = Logger<JsMapperUi>()
    }
}

private val Box2.center: Vector2 get() = max.clone().sub(min).divideScalar(2).add(min)

private fun Box3.project(camera: Camera): Box3 {
    min.project(camera)
    max.project(camera)

    // Projection may cause min/max to be swapped; correct.
    if (min.x > max.x) {
        val temp = min.x; min.x = max.x; max.x = temp
    }
    if (min.y > max.y) {
        val temp = min.y; min.y = max.y; max.y = temp
    }
    if (min.z > max.z) {
        val temp = min.z; min.z = max.z; max.z = temp
    }

    return this
}

class PanelInfo(
    val name: String,
    val faces: List<Face3>,
    val mesh: Mesh<*, *>,
    val geom: Geometry,
    val lineMaterial: LineBasicMaterial
) {
    val vertices: Set<Vector3>
        get() {
            val v = mutableSetOf<Vector3>()
            for (face in faces) {
                v.add(geom.vertices[face.a])
                v.add(geom.vertices[face.b])
                v.add(geom.vertices[face.c])
            }
            return v
        }

    val _boundingBox: Box3 by lazy {
        val boundingBox = Box3()
        for (vertex in vertices) {
            boundingBox.expandByPoint(vertex)
        }
        boundingBox.translate(mesh.getWorldPosition(Vector3()))
    }

    val boundingBox get() = _boundingBox.clone()

    private val rotator by lazy { Rotator(surfaceNormal, Vector3(0, 0, 1)) }

    fun toSurfaceNormal(point: Vector3): Vector3 {
        rotator.rotate(point); return point
    }

    private val normalBoundingBox: Box3 by lazy {
        val worldPos = mesh.getWorldPosition(Vector3())
        val boundingBox = Box3()
        for (vertex in vertices) {
            boundingBox.expandByPoint(toSurfaceNormal(vertex).add(worldPos))
        }
        boundingBox
    }

    private val normalBoundingBoxVolume: Vector3 by lazy {
        normalBoundingBox.max.clone().sub(normalBoundingBox.min)
    }

    fun toPanelSpace(point: Vector3): Vector3 {
        var pt = point.clone()
        pt = toSurfaceNormal(pt)
        pt.sub(normalBoundingBox.min)
        pt.divide(normalBoundingBoxVolume)
        return pt
    }

    fun select() {
        lineMaterial.color.r = 1.0
        lineMaterial.color.g = 0.0
    }

    fun deselect() {
        lineMaterial.color.r = 0.0
        lineMaterial.color.g = 1.0
    }

    val center get() = boundingBox.getCenter(Vector3())

    val isMultiFaced get() = faces.size > 1

    val _surfaceNormal: Vector3 by lazy {
        val faceNormalSum = Vector3()
        var totalArea = 0f
        for (face in faces) {
            val triangle = Triangle(geom.vertices[face.a], geom.vertices[face.b], geom.vertices[face.c])
            val faceArea = triangle.asDynamic().getArea() as Float
            faceNormalSum.addScaledVector(face.normal, faceArea)
            totalArea += faceArea
        }
        faceNormalSum.divideScalar(totalArea.toDouble())
    }

    val surfaceNormal get() = _surfaceNormal.clone()

    var boxOnScreen: Box2? = null
}