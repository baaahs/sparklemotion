package baaahs.mapper

import baaahs.*
import baaahs.client.ClientStorage
import baaahs.client.SceneEditorClient
import baaahs.client.document.SceneManager
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.geom.toThreeEuler
import baaahs.imaging.*
import baaahs.mapper.MappingSession.SurfaceData.PixelData
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.SceneProvider
import baaahs.sim.HostedWebApp
import baaahs.ui.Keypress
import baaahs.ui.KeypressResult
import baaahs.ui.Observable
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.globalLaunch
import baaahs.visualizer.Rotator
import baaahs.visualizer.toVector3
import js.core.jso
import kotlinx.coroutines.*
import mui.icons.material.KeyboardArrowRight
import react.RBuilder
import react.ReactElement
import react.createElement
import react.dom.br
import three.js.*
import three.js.Color
import three_ext.*
import web.canvas.CanvasImageSource
import web.canvas.CanvasRenderingContext2D
import web.canvas.ImageBitmap
import web.canvas.RenderingContextId
import web.cssom.Cursor
import web.events.Event
import web.html.HTMLCanvasElement
import web.html.HTMLElement
import web.html.HTMLImageElement
import web.prompts.prompt
import web.timers.requestAnimationFrame
import web.uievents.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.math.*
import three.js.Clock as ThreeJsClock

class MemoizedJsMapper(mapperUi: JsMapper) {
    val changedCamera = mapperUi::changedCamera
    val changedMappingStrategy = mapperUi::changedMappingStrategy
    val clickedPlay = mapperUi::clickedPlay
    val clickedPause = mapperUi::clickedPause
    val clickedRedo = mapperUi::clickedRedo
    val clickedStart = mapperUi::clickedStart
    val clickedStop = mapperUi::clickedStop
    val clickedGoToSurface = mapperUi::clickedGoToSurface
    val loadMappingSession = mapperUi::onLoadMappingSession
    val loadImage = mapperUi::loadImage
    val keyHandler = mapperUi::gotUiKeypress
}

class MapperStatus : Observable() {
    var message: String? by notifyOnChange(null)
    var message2: String? by notifyOnChange(null)
    var stats: (RBuilder.() -> Unit)? by notifyOnChange(null)
    var orderedPanels: List<Pair<JsMapper.VisibleSurface, Float>> by notifyOnChange(emptyList())
}

class JsMapper(
    plugins: Plugins,
    private val sceneEditorClient: SceneEditorClient,
    private val sceneManager: SceneManager,
    private val statusListener: StatusListener? = null,
    network: Network,
    sceneProvider: SceneProvider,
    mediaDevices: MediaDevices,
    pinkyAddress: Network.Address,
    clock: Clock,
    private val clientStorage: ClientStorage,
    mapperScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : Mapper(
    plugins, network, sceneProvider, mediaDevices, pinkyAddress, clock, mapperScope
), HostedWebApp {
    var mappingEnabled: Boolean = false
        set(value) {
            if (value != field) {
                if (value) clickedStart() else clickedStop()
                field = value
            }
        }

    override var devices: List<MediaDevices.Device> by notifyOnChange(emptyList())
    var selectedDevice: MediaDevices.Device? by notifyOnChange(null)

    // Bounds of mapper container.
    private var browserDimen = Dimen(512, 384)

    // Bounds of area used for mapping.
    private var containerDimen = browserDimen

    // Actual dimensions coming from the camera.
    private var lastCamImageDimen = browserDimen

    // Best-fit dimensions of mapping area and camera image.
    private var viewportDimen = browserDimen

    private val threeJsClock = ThreeJsClock()

    // onscreen renderer for registration UI:
    private val uiRenderer = WebGLRenderer(jso { alpha = true })
    private val uiScene = Scene()
    val uiCamera = PerspectiveCamera(45, containerDimen.aspect(), 1, 10000).also { camera ->
        camera.position.z = 1000.0
        uiScene.add(camera)
    }
    private val uiControls: CameraControls =
        CameraControls(uiCamera, uiRenderer.domElement).apply {
            dampingFactor = .5
        }
    val wireframe = Object3D()

    private val selectedSurfaces = mutableListOf<PanelInfo>()
    private var uiLocked: Boolean by notifyOnChange(false)

    private lateinit var ui2dCanvas: HTMLCanvasElement
    private val ui3dCanvas = uiRenderer.domElement
    private lateinit var savedImage: HTMLImageElement

    private lateinit var snapshotCanvas: HTMLCanvasElement
    private lateinit var baseCanvas: HTMLCanvasElement
    private lateinit var diffCanvas: HTMLCanvasElement
    private lateinit var diffCtx: CanvasRenderingContext2D
    private lateinit var panelMaskCanvas: HTMLCanvasElement
    private lateinit var perfStatsDiv: HTMLElement

    val sessions = arrayListOf<String>()
    var selectedMappingSessionName: String? by notifyOnChange(null)
    var selectedMappingSession: MappingSession? by notifyOnChange(null)

    val images = arrayListOf<String>()
    var selectedImageName: String? by notifyOnChange(null)

    var pauseButtonEnabled by notifyOnChange(true)
    var playButtonEnabled by notifyOnChange(true)

    val entitiesByName = mutableMapOf<String, Model.Entity>()
    private val entityDepictions = mutableMapOf<Model.Entity, PanelInfo>()

    private var commandProgress = ""
    private var cameraZRotation
        get() = uiCamera.getZRotation()
        set(value) { uiCamera.setZRotation(value) }

    val mapperStatus = MapperStatus()

    var redoFn: (() -> Unit)? by notifyOnChange(null)

    private var selectedEntityAndPixel: Pair<PanelInfo, Int?>? = null
    private val cameraPositions = mutableMapOf<String, CameraPosition>()

    private var points: Points<*,*>? = null
    private val raycaster = Raycaster().apply {
        params.Points = jso { threshold = 1 }
    }
    val selectedPixelIndex: Int?
        get() = selectedEntityAndPixel?.second

    private var dragging = false

    fun onMount(
        ui2dCanvas: HTMLCanvasElement,
        ui3dDiv: HTMLElement,
        snapshotCanvas: HTMLCanvasElement,
        baseCanvas: HTMLCanvasElement,
        diffCanvas: HTMLCanvasElement,
        panelMaskCanvas: HTMLCanvasElement,
        perfStatsDiv: HTMLElement,
        width: Int,
        height: Int,
        savedImage: HTMLImageElement
    ) {

        this.ui2dCanvas = ui2dCanvas
        this.savedImage = savedImage
        this.diffCanvas = diffCanvas
        this.snapshotCanvas = snapshotCanvas
        this.baseCanvas = baseCanvas
        this.diffCtx = diffCanvas.context2d()
        this.panelMaskCanvas = panelMaskCanvas
        this.perfStatsDiv = perfStatsDiv

        onResize(width, height)
        statusListener?.mapperStatusChanged(true)

        ui3dDiv.appendChild(ui3dCanvas)

        ui3dCanvas.addEventListener(MouseEvent.MOUSE_DOWN, ::mouseDown)
        ui3dCanvas.addEventListener(MouseEvent.MOUSE_MOVE, ::mouseMove)
        ui3dCanvas.addEventListener(MouseEvent.MOUSE_UP, ::mouseUp)

//        screen.focus()
//        screen.addEventListener("keydown", { event -> gotUiKeypress(event as KeyboardEvent) })

        onLaunch()
        drawAnimationFrame()
    }

    fun onUnmount() {
        ui3dCanvas.removeEventListener(MouseEvent.MOUSE_DOWN, ::mouseDown)
        ui3dCanvas.removeEventListener(MouseEvent.MOUSE_MOVE, ::mouseMove)
        ui3dCanvas.removeEventListener(MouseEvent.MOUSE_UP, ::mouseUp)

        onClose()
    }

    private fun Event.getMouse(): Vector2 {
        this as MouseEvent
        return Vector2(
            (offsetX / ui3dCanvas.width) * 2 - 1,
            -(offsetY / ui3dCanvas.height) * 2 + 1
        )
    }

    private fun updateRaycaster(event: Event) {
        val mouse = event.getMouse()
        raycaster.setFromCamera(mouse, uiCamera)
    }

    private fun selectPixel(index: Int?) {
        selectEntityPixel(selectedEntityAndPixel?.first, index)
    }

    private fun mouseDown(event: Event) {
        val (selectedEntity, selectedPixel) = selectedEntityAndPixel ?: (null to null)
        if (selectedEntity != null) {
            updateRaycaster(event)
            val clickedPixelIndex = getIntersectedPixelIndex()
            if (selectedPixel == clickedPixelIndex) {
                dragging = true
                controlsEnabled(false)
                ui3dCanvas.style.cursor = Cursor.grabbing.toString()
            } else {
                selectPixel(clickedPixelIndex)
                ui3dCanvas.style.cursor = Cursor.grab.toString()
            }
        }
    }

    private fun mouseMove(event: Event) {
        val (selectedEntity, selectedPixel) = selectedEntityAndPixel ?: (null to null)
        if (selectedEntity != null) {
            updateRaycaster(event)
            val overPixelIndex = getIntersectedPixelIndex()
            if (dragging && selectedPixel != null) {
                val moveIntersection = raycaster.intersectObject(selectedEntity.mesh)
                val toPoint = moveIntersection.firstOrNull()?.point
                if (toPoint != null) {
                    val geometry = selectedEntity.pixelsInfo.pixelsGeom
                    geometry.attributes["position"].setXYZ(selectedPixelIndex, toPoint.x, toPoint.y, toPoint.z)
                    geometry.attributes["position"].needsUpdate = true
                }
                ui3dCanvas.style.cursor = Cursor.grabbing.toString()
            } else if (overPixelIndex == null) {
                ui3dCanvas.style.cursor = Cursor.default.toString()
            } else if (overPixelIndex != selectedPixel) {
                ui3dCanvas.style.cursor = Cursor.crosshair.toString()
            } else if (overPixelIndex == selectedPixel) {
                ui3dCanvas.style.cursor = Cursor.grab.toString()
            } else {
                ui3dCanvas.style.cursor = Cursor.default.toString()
            }
        }
    }

    private fun mouseUp(event: Event) {
        dragging = false
        ui3dCanvas.style.cursor = Cursor.default.toString()
//        selectPixel(null)
        controlsEnabled(true)
    }

    private fun getIntersectedPixelIndex(): Int? {
        val intersects = raycaster.intersectObject(points as Object3D)
        return if (intersects.isEmpty()) null else {
            intersects[0].index?.toInt()
        }
    }

    private fun controlsEnabled(state: Boolean) {
        uiControls.enabled = state
    }

    fun gotUiKeypress(keypress: Keypress, event: KeyboardEvent): KeypressResult {
        var result = KeypressResult.Handled

        val code = event.code as String
        val isDigit = code.startsWith("Digit")
        if (code == "Enter") {
            processCommand(commandProgress.trim())
            commandProgress = ""
        } else if (code == "Backspace") {
            if (commandProgress.isNotEmpty()) {
                commandProgress = commandProgress.substring(0..(commandProgress.length - 2))
            }
            checkProgress()
        } else if (commandProgress.isEmpty() && code == "KeyQ") {
            adjustCameraRotation(clockwise = false, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "KeyW") {
            adjustCameraRotation(clockwise = true, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "Minus") {
            adjustCameraZoom(zoomIn = false, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "Equal") {
            adjustCameraZoom(zoomIn = true, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "ArrowUp") {
            adjustCameraY(moveUp = true, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "ArrowDown") {
            adjustCameraY(moveUp = false, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "ArrowLeft") {
            adjustCameraX(moveRight = false, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "ArrowRight") {
            adjustCameraX(moveRight = true, fine = event.shiftKey)
        } else if (commandProgress.isEmpty() && code == "Digit0") {
            resetCameraRotation()
        } else if (commandProgress.isEmpty() && isDigit && keypress.modifiers == "ctrl") {
            loadCameraPosition(code.substring(5))
        } else if (commandProgress.isEmpty() && isDigit && keypress.modifiers == "ctrl-shift") {
            saveCameraPosition(code.substring(5))
        } else if (event.key.length == 1) {
            commandProgress += event.key
            checkProgress()
        } else if (keypress.ctrlKey || keypress.metaKey) {
            result = KeypressResult.NotHandled
        }
        ui.showMessage2(commandProgress)

        return result
    }

    private fun saveCameraPosition(key: String) {
        cameraPositions[key] = CameraPosition.from(uiCamera, uiControls)
        ui.showMessage("Saved camera position `$key`.")

        globalLaunch {
            clientStorage.saveMapperData(MapperData(cameraPositions))
        }
    }

    private fun loadCameraPosition(key: String) {
        val position = cameraPositions[key]
        if (position != null) {
            position.update(uiCamera, uiControls)
            ui.showMessage("Loaded camera position from `$key`.")
        } else {
            ui.showMessage("No camera position for `$key`.")
        }
    }

    private fun checkProgress() {
        if (commandProgress.startsWith("/") && commandProgress.length > 1) {
            selectSurfacesMatching(commandProgress.substring(1))
        } else if (commandProgress.startsWith("m") && commandProgress.length > 1) {
            saveCameraPosition(commandProgress.substring(1))
            commandProgress = ""
        } else if (commandProgress.startsWith("'") && commandProgress.length > 1) {
            loadCameraPosition(commandProgress.substring(1))
            commandProgress = ""
        }
    }

    private fun resetCameraRotation() {
        cameraZRotation = 0.0
    }

    fun adjustCameraX(moveRight: Boolean, fine: Boolean = false) {
        val offset = uiControls.getFocalOffset()
        val amount = (if (moveRight) -1 else 1) * if (fine) 1 else 10
        uiControls.setFocalOffset(offset.x + amount, offset.y, offset.z, true)
    }

    fun adjustCameraY(moveUp: Boolean, fine: Boolean = false) {
        val offset = uiControls.getFocalOffset()
        val amount = (if (moveUp) 1 else -1) * if (fine) 1 else 10
        uiControls.setFocalOffset(offset.x, offset.y + amount, offset.z, true)
    }

    fun adjustCameraZoom(zoomIn: Boolean, fine: Boolean = false) {
        val amount = if (zoomIn) 1 else -1
        uiControls.dolly(if (fine) amount else amount * 10, true)
    }

    fun adjustCameraRotation(clockwise: Boolean, fine: Boolean = false) {
        val fullTurn = if (clockwise) PI * 2 else -PI * 2
        val amount = if (fine) fullTurn / 360 else fullTurn / 60
        adjustCameraZRotation(amount)
    }

    private fun adjustCameraZRotation(angle: Double) {
        cameraZRotation += angle
    }

    private fun selectSurfacesMatching(pattern: String) {
        selectedSurfaces.forEach { it.deselect() }
        selectedSurfaces.clear()
        selectedSurfaces.addAll(entityDepictions.values.filter { it.surface.name.contains(pattern, true) })
        selectedSurfaces.forEach { it.select() }
        notifyChanged()
    }

    private fun processCommand(command: String) {
        console.log(command)

        if (command.startsWith("g", ignoreCase = true) || command.startsWith("/")) {
            val surfaceName = command.substring(1).trim()
            goToSurface(surfaceName.uppercase())
        }
    }

    private fun drawAnimationFrame() {
        if (!uiLocked) {
            uiControls.update(threeJsClock.getDelta())
        }
        uiRenderer.render(uiScene, uiCamera)

        updateStats()

        requestAnimationFrame { drawAnimationFrame() }
    }

    private fun updateStats() {
        perfStatsDiv.innerText = mapperStats.summarize()
    }

    override fun render(): ReactElement<*> {
        return createElement(SceneEditorView, jso {
            sceneEditorClient = this@JsMapper.sceneEditorClient.facade
            mapper = this@JsMapper
            sceneManager = this@JsMapper.sceneManager.facade
        })
    }

    override fun onLaunch() {
        super<Mapper>.onLaunch()

        globalLaunch {
            clientStorage.loadMapperData()?.let {
                cameraPositions.putAll(it.cameraPositions)
            }
        }
    }

    override fun onClose() {
        statusListener?.mapperStatusChanged(false)

        super.onClose()
        sceneEditorClient.onClose()
    }

    override fun onNewSession(sessionName: String, mappingSession: MappingSession) {
        sessions.add(sessionName)
        selectedMappingSessionName = sessionName
        showMappingSession(mappingSession)
    }

    fun onResize(width: Int, height: Int) {
        browserDimen = Dimen(width, height)
        resize()
        notifyChanged()
    }

    private fun onCamResize(dimen: Dimen) {
        lastCamImageDimen = dimen
        resize()
        notifyChanged()
    }

    fun setSizes() {
        if (::ui2dCanvas.isInitialized)
            resize()
    }

    private fun resize() {
        containerDimen = browserDimen
        viewportDimen = containerDimen.bestFit(lastCamImageDimen)
        ui2dCanvas.resize(viewportDimen)

        uiCamera.aspect = viewportDimen.aspect()
        uiCamera.updateProjectionMatrix()

        uiRenderer.setSize(viewportDimen.width, viewportDimen.height, true)

        val thumbnailDimen = (containerDimen * .22f).bestFit(viewportDimen)
        snapshotCanvas.resize(thumbnailDimen)
        diffCanvas.resize(thumbnailDimen)
        savedImage.resize(thumbnailDimen)
        baseCanvas.resize(thumbnailDimen)
        panelMaskCanvas.resize(thumbnailDimen)
    }

    private fun HTMLCanvasElement.resize(dimen: Dimen) {
        width = dimen.width
        height = dimen.height
    }

    private fun HTMLImageElement.resize(thumbnailDimen: Dimen) {
        width = thumbnailDimen.width.toDouble()
        height = thumbnailDimen.height.toDouble()
    }

    private fun createEntityDepiction(entity: Model.Surface, vertices: Array<Vector3>): PanelInfo {
        val surface = entity

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
        entity.transform(mesh)
        mesh.name = surface.name
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
            wireframe.add(
                Line(lineGeom, lineMaterial).apply {
                    entity.transform(this)
                }
            )
        }

        geom.faces = panelFaces.toTypedArray()
        geom.computeFaceNormals()
        geom.computeVertexNormals()

        return PanelInfo(surface, panelFaces, mesh, geom, lineMaterial)
    }

    inner class PixelsInfo(private val initialPixelCount: Int) {
        internal val pixelsGeom = BufferGeometry()
        private val pixelsMaterial = PointsMaterial().apply {
            vertexColors = true
            blending = AdditiveBlending
            map = reticleTx
            transparent = true
            opacity = .8
            size = 5
            depthTest = false
        }
        val points = Points(pixelsGeom, pixelsMaterial)
        private var maxPixel = initialPixelCount
        internal val pixelDatas = mutableMapOf<Int, PixelData?>()
        private var positions = FloatArray(maxPixel * 3) { 0f }
        private var colors = FloatArray(maxPixel * 3) { 0f }
        private val positionsAttr = Float32BufferAttribute(positions, 3).also {
            it.usage = DynamicDrawUsage
            pixelsGeom.setAttribute("position", it)
        }
        private val colorsAttr = Float32BufferAttribute(colors, 3).also {
            it.usage = DynamicDrawUsage
            pixelsGeom.setAttribute("color", it)
        }

        fun setPixels(pixels: List<PixelData?>) {
            pixelDatas.clear()
            resize(pixels.size)
            pixels.forEachIndexed { index, pixelData ->
                setPixel(pixelData, index)
            }
        }

        private fun setPixel(pixelData: PixelData?, index: Int) {
            val location = pixelData?.modelPosition
            updatePixel(index, pixelData)
        }

        fun updatePixel(index: Int, pixelData: PixelData?) {
            if (index > maxPixel) {
                // Round up to the nearest power of two.
                val newMax = ceil(log2(index.toDouble())).pow(2).toInt()
                resize(newMax)
            }

            pixelDatas[index] = pixelData
            val position = pixelData?.modelPosition?.toVector3()
            setPixelLocation(index, position ?: Vector3F.origin.toVector3())
            setPixelColor(index, normalColor)
        }

        fun setPixelColor(i: Int, color: Color) {
            colors[i * 3] = color.r.toFloat()
            colors[i * 3 + 1] = color.g.toFloat()
            colors[i * 3 + 2] = color.b.toFloat()
            colorsAttr.needsUpdate = true
        }

        fun setPixelLocation(i: Int, loc: Vector3) {
            positions[i * 3] = loc.x.toFloat()
            positions[i * 3 + 1] = loc.y.toFloat()
            positions[i * 3 + 2] = loc.z.toFloat()
            positionsAttr.needsUpdate = true
        }

        fun reset() {
            pixelDatas.clear()
            resize(initialPixelCount)
        }

        private fun resize(size: Int) {
            positions = positions.resize(size * 3) { 0f }
            positionsAttr.array = positions.asDynamic()
            positionsAttr.count = size
            positionsAttr.needsUpdate = true

            colors = colors.resize(size * 3) { 0f }
            colorsAttr.array = colors.asDynamic()
            colorsAttr.count = size
            colorsAttr.needsUpdate = true
            maxPixel = size
        }
    }

    inner class PanelInfo(
        val surface: Model.Surface,
        val faces: List<Face3>,
        val mesh: Mesh<*, *>,
        val geom: Geometry,
        private val lineMaterial: LineBasicMaterial
    ) : EntityDepiction {
        override val entity: Model.Entity
            get() = surface

        private var pixelsInScene = false
        internal var pixelsInfo = PixelsInfo(surface.expectedPixelCount ?: 256).also {
            if (pixelsInScene) uiScene.add(it.points)
        }

        val pixelsInModelSpace: List<Vector3F?>
            get() {
                val vectors = mutableListOf<Vector3F?>()
                for (i in 0..(pixelsInfo.pixelDatas.keys.maxOrNull()!!)) {
                    val position = pixelsInfo.pixelDatas[i]
                    vectors.add(position?.modelPosition)
                }
                return vectors
            }

        private val vertices: Set<Vector3>
            get() {
                val v = mutableSetOf<Vector3>()
                for (face in faces) {
                    v.add(geom.vertices[face.a])
                    v.add(geom.vertices[face.b])
                    v.add(geom.vertices[face.c])
                }
                return v
            }

        private val _boundingBox: Box3 by lazy {
            val boundingBox = Box3()
            for (vertex in vertices) {
                boundingBox.expandByPoint(vertex)
            }
            boundingBox.translate(mesh.getWorldPosition(Vector3()))
        }

        val boundingBox get() = _boundingBox.clone()

        private val rotator by lazy { Rotator(surfaceNormal, vector3FacingForward) }

        private fun toSurfaceNormal(point: Vector3): Vector3 {
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
            lineMaterial.color.g = 1.0
        }

        fun deselect() {
            lineMaterial.color.r = 0.0
            lineMaterial.color.g = 1.0
        }

        val center get() = boundingBox.getCenter(Vector3())

        val isMultiFaced get() = faces.size > 1

        private val _surfaceNormal: Vector3 by lazy {
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

        override fun setPixel(index: Int, modelPosition: Vector3F?/*, deltaImage: String? = null*/) {
            pixelsInfo.updatePixel(index, PixelData(modelPosition))
        }

        override fun setPixels(pixels: List<PixelData?>) {
            pixelsInfo.setPixels(pixels)
        }

        override fun showPixels() {
            if (!pixelsInScene) {
                uiScene.add(pixelsInfo.points)
                this@JsMapper.points = pixelsInfo.points
                pixelsInScene = true
            }
        }

        fun hidePixels() {
            if (pixelsInScene) {
                uiScene.remove(pixelsInfo.points)
                this@JsMapper.points = null
                pixelsInScene = false
            }
        }

        override fun resetPixels() {
            pixelsInfo.reset()
        }

        fun getPixelData(pixelIndex: Int): PixelData? =
            pixelsInfo.pixelDatas[pixelIndex]

        fun selectPixel(pixelIndex: Int) {
            pixelsInfo.setPixelColor(pixelIndex, selectedColor)
        }

        fun deselectPixel(pixelIndex: Int) {
            pixelsInfo.setPixelColor(pixelIndex, normalColor)
        }
    }


    fun findVisualizer(entityName: String): PanelInfo? =
        entitiesByName[entityName]?.let { entityDepictions[it] }

    override val ui: MapperUi = object : MapperUi {
        override var message: String?
            get() = mapperStatus.message
            set(value) { mapperStatus.message = value }
        override var message2: String?
            get() = mapperStatus.message2
            set(value) { mapperStatus.message2 = value }

        override fun addWireframe(model: Model) {
            entitiesByName.clear()
            entityDepictions.clear()
            resetScene()

            val geometries = mutableMapOf<Model.Geometry, Array<Vector3>>()

            model.visit { entity ->
                entitiesByName[entity.name] = entity

                // TODO: Add wireframe depiction for other entity types.
                if (entity is Model.Surface) {
                    val vertices = geometries.getOrPut(entity.geometry) {
                        entity.geometry.vertices
                            .map { v -> Vector3(v.x, v.y, v.z) }
                            .toTypedArray()
                    }
                    entityDepictions[entity] = createEntityDepiction(entity, vertices)
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

        override fun showLiveCamImage(image: Image, changeRegion: MediaDevices.Region?) =
            this@JsMapper.showLiveCamImage(image, changeRegion)

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

        override fun setRedo(fn: (suspend () -> Unit)?) {
            if (fn == null) {
                redoFn = null
            } else {
                redoFn = { GlobalScope.launch { fn() } }
            }
        }

        override fun lockUi(): CameraPosition {
            uiLocked = true
            return CameraPosition.from(uiCamera, uiControls)
        }

        override fun unlockUi() {
            uiLocked = false
        }

        override fun getAllSurfaceVisualizers(): List<EntityDepiction> {
            return entityDepictions.values.toList()
        }

        override fun getVisibleSurfaces(): List<Mapper.VisibleSurface> {
            val visibleSurfaces = mutableListOf<Mapper.VisibleSurface>()
            val screenBox = getScreenBox()
            val screenCenter = screenBox.center
            val fixedCamera = uiCamera.clone() as PerspectiveCamera

            entityDepictions.forEach { (entity, panelInfo) ->
                val panelPosition = panelInfo.geom.vertices[panelInfo.faces[0].a]
                val dirToCamera = uiCamera.position.clone().sub(panelPosition)
                dirToCamera.normalize()
                val angle = panelInfo.faces[0].normal.dot(dirToCamera)
                if (angle > 0) {
                    panelInfo.mesh.updateMatrixWorld()

                    val panelBoundingBox = panelInfo.boundingBox.project(uiCamera)
                    val panelBoxOnScreen = calcBoundingBoxOnScreen(panelBoundingBox, screenCenter)
                    panelInfo.boxOnScreen = panelBoxOnScreen
                    if (panelBoxOnScreen.intersectsBox(screenBox)) {
                        val region = MediaDevices.Region(
                            panelBoxOnScreen.min.x.roundToInt(),
                            panelBoxOnScreen.min.y.roundToInt(),
                            panelBoxOnScreen.max.x.roundToInt(),
                            panelBoxOnScreen.max.y.roundToInt(),
                            viewportDimen
                        )
                        visibleSurfaces.add(VisibleSurface(entity, region, panelInfo, fixedCamera))
                    }
                }
            }

            return visibleSurfaces
        }

        override fun showCandidates(orderedPanels: List<Pair<Mapper.VisibleSurface, Float>>) {
            orderedPanels as List<Pair<VisibleSurface, Float>>

            val firstGuess = orderedPanels.first()
            (firstGuess.first.panelInfo.mesh.material as MeshBasicMaterial).color.r += .25

            mapperStatus.orderedPanels = orderedPanels
        }

        override fun intersectingSurface(uv: Uv, visibleSurfaces: List<Mapper.VisibleSurface>): Mapper.VisibleSurface? {
            val raycaster = Raycaster()
            val pixelVector = uv.toVector2()
            raycaster.setFromCamera(pixelVector, uiCamera)
            val intersections = raycaster.intersectObject(uiScene, true)
            return if (intersections.isNotEmpty()) {
                val intersect = intersections.first()
                visibleSurfaces.find { it.entity.name == intersect.`object`.name }
            } else {
                null
            }
        }

        override fun showStats(total: Int, mapped: Int, visible: Int) {
            mapperStatus.stats = {
                KeyboardArrowRight {}
                +"Mapped: $mapped / $total"
                br {}
                +"Visible: $visible"
            }
        }

        override fun close() {
        }

        override fun addExistingSession(name: String) {
            sessions.add(name)
            notifyChanged()
        }

        override fun pauseForUserInteraction() {
            clickedPause()
        }
    }

    private fun resetScene() {
        uiScene.clear()
        uiScene.add(uiCamera)
    }

    inner class VisibleSurface(
        override val entity: Model.Entity,
        override val boxOnScreen: MediaDevices.Region,
        val panelInfo: PanelInfo,
        val camera: PerspectiveCamera
    ) : Mapper.VisibleSurface {
        override val pixelsInModelSpace: List<Vector3F?> get() = panelInfo.pixelsInModelSpace

        override fun setPixel(pixelIndex: Int, uv: Uv) {
            val intersect = findIntersection(uv)
            val positionInModel = intersect?.point

            panelInfo.setPixel(pixelIndex, positionInModel?.toVector3F())
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
                logger.warn { "Couldn't find point in ${entity.name}, searching in scene..." }
                intersections = raycaster.intersectObject(uiScene, true)
                console.log("Found intersections: ", intersections)
            }
            return if (intersections.isNotEmpty()) {
                intersections.first()
            } else {
                null
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

    fun showLiveCamImage(image: Image, changeRegion: MediaDevices.Region?) {
        if (image.dimen != lastCamImageDimen) {
            onCamResize(image.dimen)
        }

        paintCamImage(image)

        changeRegion?.apply {
            val ui2dCtx = ui2dCanvas.getContext(RenderingContextId.canvas) as CanvasRenderingContext2D
            ui2dCtx.lineWidth = 2.0
            ui2dCtx.strokeStyle = "#ff0000"
            ui2dCtx.strokeRect(x0.toDouble(), y0.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    private fun paintCamImage(image: Image) {
        if (!::ui2dCanvas.isInitialized) {
            return
        }
        CanvasBitmap(ui2dCanvas).drawImage(
            image,
            0, 0, image.width, image.height,
            0, 0, viewportDimen.width, viewportDimen.height,
        )
    }

    private fun HTMLCanvasElement.showImage(bitmap: Bitmap, changeRegion: MediaDevices.Region? = null) {
        console.log("Draw ", bitmap, " to ", this)
        val ctx2d = getContext(RenderingContextId.canvas) as CanvasRenderingContext2D
        ctx2d.resetTransform()
        val renderBitmap = when (bitmap) { // TODO: huh?
            is CanvasBitmap -> bitmap.canvas
            else -> bitmap as CanvasImageSource
        }
        val drawSize = Dimen(width, height).bestFit(bitmap.dimen)
        ctx2d.drawImage(renderBitmap, 0.0, 0.0, drawSize.width.toDouble(), drawSize.height.toDouble())

        changeRegion?.apply {
            if (changeRegion.changedAmount > .01) {
                ctx2d.strokeStyle = "#ff0000"
            } else {
                ctx2d.strokeStyle = "#ffff00"
            }
            ctx2d.lineWidth = 1.0

            val drawScaleX = drawSize.width / bitmap.width.toDouble()
            val drawScaleY = drawSize.height / bitmap.height.toDouble()

            ctx2d.strokeRect(
                x0.toDouble() * drawScaleX - 1, y0.toDouble() * drawScaleY - 1,
                changeRegion.width * drawScaleX + 2, changeRegion.height * drawScaleY + 2
            )
        }
    }

    fun changedCamera(device: MediaDevices.Device?) {
        selectedDevice = device
        useCamera(device)
    }

    fun changedMappingStrategy(mappingStrategy: MappingStrategy) {
        this.mappingStrategy = mappingStrategy
        notifyChanged()
    }

    fun clickedPlay() {
        showPauseMode(false)
        onStart()
    }

    fun clickedPause() {
        showPauseMode(true)
        onPause()
    }

    fun clickedRedo() {
        redoFn?.invoke()
    }

    private fun showPauseMode(isPaused: Boolean) {
        pauseButtonEnabled = !isPaused
        playButtonEnabled = isPaused
        notifyChanged()
    }

    fun clickedStart() {
        onStart()
    }

    fun clickedStop() {
        onStop()
    }

    fun clickedGoToSurface() {
        val surfaceName = prompt("Surface:")
        if (surfaceName != null && surfaceName.isNotEmpty()) {
            goToSurface(surfaceName.uppercase())
        }
    }

    fun onLoadMappingSession(sessionName: String?) {
        selectedMappingSessionName = sessionName

        if (sessionName != null) {
            globalLaunch {
                val session = loadMappingSession(sessionName)
                showMappingSession(session)
            }

            val sessionId = sessionName.findSessionId()
            refreshImages(sessionId)
        }
    }

    private fun refreshImages(sessionId: String?) {
        globalLaunch {
            images.clear()
            mapperBackend.listImages(sessionId).forEach {
                images.add(it)
            }

            notifyChanged()
        }
    }

    private fun showMappingSession(session: MappingSession) {
        val surfaceVisualizers = ui.getAllSurfaceVisualizers().associateBy {
            it.resetPixels()
            it.entity.name
        }

        session.surfaces.forEach { surfaceData ->
            val surfaceVisualizer = surfaceVisualizers.getBang(surfaceData.entityName, "visible surface")
            surfaceData.pixels?.let { pixels ->
                surfaceVisualizer.setPixels(pixels)
            }

            surfaceVisualizer.showPixels()
        }

        selectedMappingSession = session
    }

    fun loadImage(name: String?, showChangeRegion: Boolean = false) {
        if (name == null) {
            ui.showDiffImage(createWritableBitmap(0, 0))
        } else {
            globalLaunch {
                val img = loadMapperImage(name)
                val bitmap = ImageBitmapImage(
                    kotlinx.browser.window.createImageBitmap(img).await()
                        .unsafeCast<ImageBitmap>()
                ).toBitmap()

                selectedImageName = name

                var pixelChangeRegion: MediaDevices.Region? = null

                if (showChangeRegion) {
                    val analysis = ImageProcessing.analyze(bitmap)
                    pixelChangeRegion = analysis.detectChangeRegion(.9f)
                }

//                delay(100)
                ui.showDiffImage(bitmap, changeRegion = pixelChangeRegion)
            }
        }
    }

    fun selectEntityPixel(entityName: String?, index: Int?) {
        val entityToSelect = entityName?.let { findVisualizer(it) }
        selectEntityPixel(entityToSelect, index)
    }

    private fun deselectEntityPixel() {
        selectedEntityAndPixel?.let { (entity, pixelIndex) ->
            entity.deselect()
            if (pixelIndex != null) entity.deselectPixel(pixelIndex)
            hideImage()
        }
    }

    private fun selectEntityPixel(
        panelInfo: PanelInfo?,
        index: Int?
    ) {
        deselectEntityPixel()

        if (panelInfo == null) return
        panelInfo.select()
        if (index != null) {
            panelInfo.selectPixel(index)

            val pixelData = panelInfo.getPixelData(index)
            pixelData?.metadata?.let { pixelMetadata ->
                when (pixelMetadata) {
                    is OneAtATimeMappingStrategy.OneAtATimePixelMetadata -> {
                        pixelMetadata.deltaImage?.let { loadImage(it, true) }
                    }
                    is TwoLogNMappingStrategy.TwoLogNPixelMetadata -> {
                        (pixelMetadata.singleImage ?: pixelMetadata.calculatedImage)?.let { loadImage(it, true) }
                    }
                    else -> error("Unknown pixel metadata $pixelMetadata.")
                }
            }
            udpSockets.pixelOnByBroadcast(index)
        } else {
            udpSockets.allDark()
        }

        selectedEntityAndPixel = panelInfo to index

        notifyChanged()
    }

    private suspend fun loadMapperImage(imageName: String): org.w3c.dom.Image {
        val deferred = CompletableDeferred<Unit>()

        return org.w3c.dom.Image().apply() {
            this.src = getImageUrl(imageName)
            this.title = imageName
            this.style.display = "block"
            this.onload = { deferred.complete(Unit) }
        }.also {
            deferred.await()
        }
    }

    suspend fun getImageUrl(imageName: String) =
        (mapperBackend.getImageUrl(imageName)
            ?: error("No url for \"$imageName\"."))

    private fun hideImage() {
        savedImage.src = ""
        savedImage.title = ""
        savedImage.style.display = "none"
    }

    private fun goToSurface(name: String) {
        val surface = entityDepictions.keys.find { it.name == name }
        if (surface != null) {
            val panelInfo = entityDepictions[surface]!!
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

    interface StatusListener {
        fun mapperStatusChanged(isRunning: Boolean)
    }

    companion object {
        internal val logger = Logger<JsMapper>()

        val normalColor = Color(0, 0, 1)
        val selectedColor = Color(1, 1, 0)

        val reticleTx = TextureLoader().load(
            "$resourcesBase/visualizer/textures/reticle.webp",
            { println("loaded!") },
            { println("progress!") },
            { println("error!") }
        )
    }
}

private fun Model.Entity.transform(obj: Object3D) {
    obj.position.copy(position.toVector3())
    obj.rotation.copy(rotation.toThreeEuler())
    obj.scale.copy(scale.toVector3())
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

fun Uv.toVector2() = Vector2(
    u * 2 - 1,
    -(v * 2 - 1)
)

fun String.findSessionId(): String {
    val v0Index = indexOf("-v")
    return if (v0Index > -1) substring(0, v0Index) else error("$this isn't a session file name?")
}