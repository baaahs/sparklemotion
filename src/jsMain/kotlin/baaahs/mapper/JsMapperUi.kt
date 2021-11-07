package baaahs.mapper

import baaahs.MediaDevices
import baaahs.admin.AdminClient
import baaahs.context2d
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.getValue
import baaahs.imaging.*
import baaahs.imaging.Image
import baaahs.model.Model
import baaahs.sim.HostedWebApp
import baaahs.ui.Observable
import baaahs.ui.value
import baaahs.util.Logger
import baaahs.visualizer.Rotator
import baaahs.visualizer.toVector3
import baaahs.window
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.RBuilder
import react.ReactElement
import react.createElement
import react.dom.br
import react.dom.i
import three.js.*
import three_ext.CameraControls
import three_ext.Float32BufferAttribute
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
    val clickedStart = mapperUi::clickedStart
    val clickedStop = mapperUi::clickedStop
    val clickedGoToSurface = mapperUi::clickedGoToSurface
    val loadMappingSession = mapperUi::loadMappingSession
    val onKeydown = { event: Event -> mapperUi.gotUiKeypress(event as KeyboardEvent) }
}

class MapperStatus : Observable() {
    var message: String? by notifyOnChange(null)
    var message2: String? by notifyOnChange(null)
    var stats: (RBuilder.() -> Unit)? by notifyOnChange(null)
    var orderedPanels: List<Pair<JsMapperUi.VisibleSurface, Float>> by notifyOnChange(emptyList())
}

class JsMapperUi(
    private val adminClient: AdminClient,
    private val statusListener: StatusListener? = null
) : Observable(), MapperUi, HostedWebApp {
    private lateinit var listener: MapperUi.Listener

    override var devices: List<MediaDevices.Device> by notifyOnChange(emptyList())
    var selectedDevice: MediaDevices.Device? by notifyOnChange(null)

    override fun listen(listener: MapperUi.Listener) {
        this.listener = listener
    }

    // Bounds of mapper container.
    var browserDimen = Dimen(512, 384)

    // Bounds of area used for mapping.
    var containerDimen = browserDimen

    // Actual dimensions coming from the camera.
    var lastCamImageDimen = browserDimen

    // Best-fit dimensions of mapping area and camera image.
    var viewportDimen = browserDimen

    private val clock = Clock()

    // onscreen renderer for registration UI:
    private val uiRenderer = WebGLRenderer(jsObject { alpha = true })
    private val uiScene = Scene()
    private val uiCamera = PerspectiveCamera(45, containerDimen.aspect(), 1, 10000).also { camera ->
        camera.position.z = 1000.0
        uiScene.add(camera)
    }
    private val uiControls: CameraControls = CameraControls(uiCamera, uiRenderer.domElement)
    val wireframe = Object3D()

    private val selectedSurfaces = mutableListOf<PanelInfo>()
    var uiLocked: Boolean by notifyOnChange(false)

    private lateinit var ui2dCanvas: HTMLCanvasElement

    private val ui3dCanvas = uiRenderer.domElement

    private lateinit var snapshotCanvas: HTMLCanvasElement
    private lateinit var baseCanvas: HTMLCanvasElement
    private lateinit var diffCanvas: HTMLCanvasElement
    private lateinit var diffCtx: CanvasRenderingContext2D
    private lateinit var panelMaskCanvas: HTMLCanvasElement
    private lateinit var perfStatsDiv: HTMLElement

    val sessions = arrayListOf<String>()
    var selectedMappingSession: String? by notifyOnChange(null)

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
        snapshotCanvas: HTMLCanvasElement,
        baseCanvas: HTMLCanvasElement,
        diffCanvas: HTMLCanvasElement,
        panelMaskCanvas: HTMLCanvasElement,
        perfStatsDiv: HTMLElement,
        width: Int,
        height: Int
    ) {
        this.ui2dCanvas = ui2dCanvas
        this.diffCanvas = diffCanvas
        this.snapshotCanvas = snapshotCanvas
        this.baseCanvas = baseCanvas
        this.diffCtx = diffCanvas.context2d()
        this.panelMaskCanvas = panelMaskCanvas
        this.perfStatsDiv = perfStatsDiv

        onResize(width, height)
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
        selectedSurfaces.addAll(modelSurfaceInfos.values.filter { it.surface.name.contains(pattern, true) })
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

        updateStats()

        window.requestAnimationFrame { drawAnimationFrame() }
    }

    private fun updateStats() {
        perfStatsDiv.innerText = Mapper.mapperStats.summarize()
    }

    override fun render(): ReactElement {
        return createElement(MapperIndexView, jsObject<MapperIndexViewProps> {
            adminClient = this@JsMapperUi.adminClient.facade
            mapperUi = this@JsMapperUi
        })
    }

    override fun onLaunch() {
        listener.onLaunch()
    }

    override fun onClose() {
        statusListener?.mapperStatusChanged(false)

        listener.onClose()
        adminClient.onClose()
    }

    fun onResize(width: Int, height: Int) {
        browserDimen = Dimen(width, height)
        resize()
    }

    private fun onCamResize(dimen: Dimen) {
        lastCamImageDimen = dimen
        resize()
    }

    private fun resize() {
        containerDimen = browserDimen
        viewportDimen = containerDimen.bestFit(lastCamImageDimen)
        ui2dCanvas.resize(viewportDimen)
        ui2dCanvas.width = viewportDimen.width
        ui2dCanvas.height = viewportDimen.height

        uiCamera.aspect = viewportDimen.aspect()
        uiCamera.updateProjectionMatrix()

        uiRenderer.setSize(viewportDimen.width, viewportDimen.height, true)

        val thumbnailDimen = (containerDimen * .22f).bestFit(viewportDimen)
        snapshotCanvas.resize(thumbnailDimen)
        diffCanvas.resize(thumbnailDimen)
        baseCanvas.resize(thumbnailDimen)
        panelMaskCanvas.resize(thumbnailDimen)

        notifyChanged()
    }

    private fun HTMLCanvasElement.resize(dimen: Dimen) {
        width = dimen.width
        height = dimen.height
    }

    override fun addWireframe(model: Model) {
        model.visitEntities { modelVertices, entities ->
            val vertices = modelVertices?.map { v -> Vector3(v.x, v.y, v.z) }?.toTypedArray()

            entities.forEach { entity ->
                // TODO: Add wireframe for other entity types.
                if (entity is Model.Surface) {
                    val surface = entity

                    if (vertices == null) error("No vertices for surface ${entity.name}!")
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
                            pt.toVector3() + surfaceNormal
                        }.toTypedArray())
                        wireframe.add(Line(lineGeom, lineMaterial))
                    }

                    geom.faces = panelFaces.toTypedArray()
                    geom.computeFaceNormals()
                    geom.computeVertexNormals()

                    modelSurfaceInfos[surface] =
                        PanelInfo(surface, panelFaces, mesh, geom, lineMaterial)

                }
            }
        }

        uiScene.add(wireframe)

        val originMarker = Mesh(
            SphereBufferGeometry(1, 32, 32),
            MeshBasicMaterial().apply { color = Color(0xff0000) })
        uiScene.add(originMarker)

        val boundingBox = Box3().setFromObject(wireframe)
        uiControls.fitToBox(boundingBox, false)
    }

    inner class PanelInfo(
        val surface: Model.Surface,
        val faces: List<Face3>,
        val mesh: Mesh<*, *>,
        val geom: Geometry,
        val lineMaterial: LineBasicMaterial
    ) : MapperUi.SurfaceViz {
        override val modelSurface: Model.Surface
            get() = surface

        private val pixelsGeom = BufferGeometry()
        private val pixelsMaterial = PointsMaterial().apply {
            color = Color(0x00FF00)
            size = 5
        }
        private var pixelsInScene = true
        private val pixelsPoints = Points(pixelsGeom, pixelsMaterial).also {
            if (pixelsInScene) uiScene.add(it)
        }
        private val pixelLocations = mutableMapOf<Int, Vector3?>()
        private var maxPixel = -1

        val pixelsInModelSpace: List<Vector3F?>
            get() {
                val vectors = mutableListOf<Vector3F?>()
                for (i in 0..(pixelLocations.keys.maxOrNull()!!)) {
                    val position = pixelLocations[i]
                    vectors.add(position?.let {
                        Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat())
                    })
                }
                return vectors
            }

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
                val faceArea = triangle.getArea() as Float
                faceNormalSum.addScaledVector(face.normal, faceArea)
                totalArea += faceArea
            }
            faceNormalSum.divideScalar(totalArea.toDouble())
        }

        val surfaceNormal get() = _surfaceNormal.clone()

        var boxOnScreen: Box2? = null

        override fun setPixel(index: Int, modelPosition: Vector3F?) {
            pixelLocations[index] = modelPosition?.toVector3()
            if (index > maxPixel) maxPixel = index
            updatePixels()
        }

        override fun showPixels() {
            if (!pixelsInScene) {
                uiScene.add(pixelsPoints)
                pixelsInScene = true
            }
        }

        fun hidePixels() {
            if (pixelsInScene) {
                uiScene.remove(pixelsPoints)
                pixelsInScene = false
            }
        }

        override fun resetPixels() {
            pixelLocations.clear()
            maxPixel = -1
            updatePixels()
        }

        fun updatePixels() {
            if (pixelsInScene) {
                val positions = Array((maxPixel + 1) * 3) { 0f }
                (0 until maxPixel).forEach { i ->
                    val loc = pixelLocations[i] ?: Vector3(0, 0, 0)
                    positions[i * 3] = loc.x.toFloat()
                    positions[i * 3 + 1] = loc.y.toFloat()
                    positions[i * 3 + 2] = loc.z.toFloat()
                }

                pixelsGeom.setAttribute("position", Float32BufferAttribute(positions, 3))
            }
        }
    }


    override fun lockUi(): MapperUi.CameraOrientation {
        uiLocked = true
        return CameraOrientation.from(uiCamera)
    }

    override fun unlockUi() {
        uiLocked = false
    }

    override fun getAllSurfaceVisualizers(): List<MapperUi.SurfaceViz> {
        return modelSurfaceInfos.values.toList()
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
                        panelBoxOnScreen.max.y.roundToInt(),
                        viewportDimen
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

        override val pixelsInModelSpace: List<Vector3F?> get() = panelInfo.pixelsInModelSpace

        override fun setPixel(pixelIndex: Int, uv: Uv) {
            val intersect = findIntersection(uv)
            val positionInModel = intersect?.point

            panelInfo.setPixel(pixelIndex, positionInModel?.toVector3F())
        }

        override fun setPixel(pixelIndex: Int, panelSpacePosition: Vector3F?) {
            panelInfo.setPixel(pixelIndex, panelSpacePosition)
        }

        override fun translatePixelToPanelSpace(uv: Uv): Vector2F? {
            val intersection = findIntersection(uv) ?: return null

            val point = panelInfo.toPanelSpace(intersection.point.clone())
            console.log("   ---->", point.x, point.y, point.z)
            return Vector2F(point.x.toFloat(), point.y.toFloat())
        }

        private fun findIntersection(uv: Uv): Intersection? {
            val raycaster = Raycaster()
            raycaster.setFromCamera(uv.toVector2(), camera)
            var intersections = raycaster.intersectObject(panelInfo.mesh, false)
            if (intersections.isEmpty()) {
                logger.warn { "Couldn't find point in ${modelSurface.name}, searching in scene..." }
                intersections = raycaster.intersectObject(uiScene, true)
                console.log("Found intersections: ", intersections)
            }
            if (intersections.isNotEmpty()) {
                return intersections.first()
            } else {
                return null
            }
        }

        override fun showPixels() {
            panelInfo.showPixels()
        }

        override fun hidePixels() {
            panelInfo.hidePixels()
        }

        override fun resetPixels() {
            panelInfo.resetPixels()
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

    override fun intersectingSurface(uv: Uv, visibleSurfaces: List<MapperUi.VisibleSurface>): MapperUi.VisibleSurface? {
        val raycaster = Raycaster()
        val pixelVector = uv.toVector2()
        raycaster.setFromCamera(pixelVector, uiCamera)
        val intersections = raycaster.intersectObject(uiScene, true)
        return if (intersections.isNotEmpty()) {
            val intersect = intersections.first()
            visibleSurfaces.find { it.modelSurface.name == intersect.`object`.name }
        } else {
            null
        }
    }

    private fun getScreenBox(): Box2 {
        // Workaround for Kotlin/JS bug:
        return Box2(Vector2(0, 0), Vector2(browserDimen.width, browserDimen.height))
//        return Box2(Vector2(0, 0), browserDimen.toVector2())
    }

    private fun calcBoundingBoxOnScreen(box: Box3, screenCenter: Vector2): Box2 {
        val minX = ((box.min.x * screenCenter.x) + screenCenter.x).toInt()
        val maxX = ((box.max.x * screenCenter.x) + screenCenter.x).toInt()

        // Invert Y for screen coordinates.
        val minY = ((-box.max.y * screenCenter.y) + screenCenter.y).toInt()
        val maxY = ((-box.min.y * screenCenter.y) + screenCenter.y).toInt()

        return Box2(Vector2(minX, minY), Vector2(maxX, maxY))
    }

    override fun showLiveCamImage(image: Image, changeRegion: MediaDevices.Region?) {
        if (image.dimen != lastCamImageDimen) {
            onCamResize(image.dimen)
        }

        CanvasBitmap(ui2dCanvas).drawImage(
            image,
            0, 0, image.width, image.height,
            0, 0, viewportDimen.width, viewportDimen.height,
        )

        changeRegion?.apply {
            val ui2dCtx = ui2dCanvas.getContext("2d") as CanvasRenderingContext2D
            ui2dCtx.lineWidth = 2.0
            ui2dCtx.strokeStyle = "#ff0000"
            ui2dCtx.strokeRect(x0.toDouble(), y0.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    override fun showSnapshot(bitmap: Bitmap) =
        snapshotCanvas.showImage(bitmap)

    override fun showBaseImage(bitmap: Bitmap) =
        baseCanvas.showImage(bitmap)

    override fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region?) =
        diffCanvas.showImage(deltaBitmap, changeRegion)

    override fun showPanelMask(bitmap: Bitmap, changeRegion: MediaDevices.Region?) =
        panelMaskCanvas.showImage(bitmap, changeRegion)

    override fun showMessage(message: String) {
        mapperStatus.message = message
        logger.info { "Message: $message" }
    }

    override fun showMessage2(message: String) {
        mapperStatus.message2 = message
    }

    private fun HTMLCanvasElement.showImage(bitmap: Bitmap, changeRegion: MediaDevices.Region? = null) {
        val ctx2d = getContext("2d") as CanvasRenderingContext2D
        ctx2d.resetTransform()
        val renderBitmap = when (bitmap) { // TODO: huh?
            is NativeBitmap -> bitmap.canvas
            is CanvasBitmap -> bitmap.canvas
            else -> bitmap as CanvasImageSource
        }
        val drawSize = Dimen(width, height).bestFit(bitmap.dimen)
        ctx2d.drawImage(renderBitmap, 0.0, 0.0, drawSize.width.toDouble(), drawSize.height.toDouble())

        changeRegion?.apply {
            ctx2d.strokeStyle = "#ff0000"
            ctx2d.lineWidth = 1.0

            val drawScaleX = drawSize.width / bitmap.width.toDouble()
            val drawScaleY = drawSize.height / bitmap.height.toDouble()

            ctx2d.strokeRect(
                x0.toDouble() * drawScaleX - 1, y0.toDouble() * drawScaleY - 1,
                changeRegion.width * drawScaleX + 2, changeRegion.height * drawScaleY + 2
            )
        }
    }

    override fun setRedo(fn: (suspend () -> Unit)?) {
        if (fn == null) {
            redoFn = null
        } else {
            redoFn = { GlobalScope.launch { fn() } }
        }
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
        selectedDevice = devices.find { it.deviceId == selectedDeviceId }

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

    fun clickedStart() {
        listener.onStart()
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

    fun loadMappingSession(event: Event) {
        val mappingSessionToLoad = event.target?.value
        selectedMappingSession = mappingSessionToLoad

        listener.loadMappingSession(mappingSessionToLoad)
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

private fun Vector3.toVector3F() = Vector3F(x.toFloat(), y.toFloat(), z.toFloat())

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

fun Uv.toVector2() = Vector2(
    u * 2 - 1,
    -(v * 2 - 1)
)