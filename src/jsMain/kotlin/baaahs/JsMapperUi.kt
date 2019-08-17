package baaahs

import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.imaging.Bitmap
import baaahs.imaging.CanvasBitmap
import baaahs.imaging.Image
import baaahs.imaging.NativeBitmap
import baaahs.visualizer.Rotator
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.*
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.*
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.table
import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.dom.clear
import kotlin.math.min
import kotlin.math.roundToInt

class JsMapperUi(private val statusListener: StatusListener? = null) : MapperUi, HostedWebApp {
    private lateinit var listener: MapperUi.Listener

    override fun listen(listener: MapperUi.Listener) {
        this.listener = listener
    }

    private var width = 512
    private var height = 384

    private var uiWidth = 512
    private var uiHeight = 384

    private var haveCamDimensions = false
    private var camWidth = 0
    private var camHeight = 0

    private val clock = Clock()

    // onscreen renderer for registration UI:
    private val uiRenderer = WebGLRenderer(js("{alpha: true}"))
    private var uiScene = Scene()
    private var uiCamera = PerspectiveCamera(45, width.toDouble() / height, 1, 10000)
    private var uiControls: dynamic
    private val wireframe = Object3D()

    private var selectedSurfaces = mutableListOf<PanelInfo>()
    private var uiLocked: Boolean = false

    private val screen = document.create.div("mapperUi-screen") {
        tabIndex = "-1" // So we can receive key events.

        div("mapperUi-controls") {
            button { +"▲"; onClickFunction = { wireframe.position.y += 10 } }
            button { +"▼"; onClickFunction = { wireframe.position.y -= 10 } }
//            button { i(classes="fas fa-crosshairs"); onClickFunction = { target() } }
            button { i(classes = "fas fa-play"); onClickFunction = { clickedPlay() } }
            button { i(classes = "fas fa-pause"); onClickFunction = { clickedPause() } }
            button { i(classes = "fas fa-redo"); onClickFunction = { redoFn?.invoke() } }
            button { i(classes = "fas fa-stop"); onClickFunction = { clickedStop() } }
            button {
                i(classes = "fas fa-sign-in-alt")
                onClickFunction = {
                    val surfaceName = window.prompt("Surface:")
                    if (surfaceName != null && surfaceName.isNotEmpty()) {
                        goToSurface(surfaceName.toUpperCase())
                    }
                }
            }
            select("mapperUi-sessionSelector") { }
        }
        canvas(classes = "mapperUi-2d-canvas") {
            width = this@JsMapperUi.width.toString() + "px"
            height = this@JsMapperUi.height.toString() + "px"
        }
        div("mapperUi-3d-div") { }
        canvas(classes = "mapperUi-diff-canvas") {
            width = this@JsMapperUi.width.toString() + "px"
            height = this@JsMapperUi.height.toString() + "px"
        }
        canvas(classes = "mapperUi-before-canvas") {
            width = this@JsMapperUi.width.toString() + "px"
            height = this@JsMapperUi.height.toString() + "px"
        }
        canvas(classes = "mapperUi-after-canvas") {
            width = this@JsMapperUi.width.toString() + "px"
            height = this@JsMapperUi.height.toString() + "px"
        }
        div("mapperUi-stats") { }
        div("mapperUi-message") { }
        div("mapperUi-message2") { }
        div("mapperUi-table") { }
    }

    private val ui2dCanvas = screen.first<HTMLCanvasElement>("mapperUi-2d-canvas")
    private val ui2dCtx = ui2dCanvas.context2d()

    private val ui3dDiv = screen.first<HTMLCanvasElement>("mapperUi-3d-div")
    private val ui3dCanvas = uiRenderer.domElement as HTMLCanvasElement

    private val diffCanvas = screen.first<HTMLCanvasElement>("mapperUi-diff-canvas")
    private val diffCtx = diffCanvas.context2d()

    private val beforeCanvas = screen.first<HTMLCanvasElement>("mapperUi-before-canvas")
    private val afterCanvas = screen.first<HTMLCanvasElement>("mapperUi-after-canvas")

    private val statsDiv = screen.first<HTMLDivElement>("mapperUi-stats")
    private val messageDiv = screen.first<HTMLDivElement>("mapperUi-message")
    private val message2Div = screen.first<HTMLDivElement>("mapperUi-message2")
    private val table = screen.first<HTMLDivElement>("mapperUi-table")
    private val sessionSelector = screen.first<HTMLSelectElement>("mapperUi-sessionSelector")

    private val playButton = screen.first<HTMLButtonElement>("fa-play")
    private val pauseButton = screen.first<HTMLButtonElement>("fa-pause")
    private val redoButton = screen.first<HTMLButtonElement>("fa-redo")

    private val modelSurfaceInfos = mutableMapOf<SheepModel.Panel, PanelInfo>()

    private var commandProgress = ""
    private var cameraZRotation = 0f

    private var redoFn: (() -> Unit)? = null

    init {
        statusListener?.mapperStatusChanged(true)

        ui3dDiv.appendChild(ui3dCanvas)

        uiCamera.position.z = 1000.0
        uiScene.add(uiCamera)

        uiControls = js("document.createCameraControls")(uiCamera, uiRenderer.domElement)

        screen.focus()
        screen.addEventListener("keydown", { event -> gotUiKeypress(event as KeyboardEvent) })

        drawAnimationFrame()
    }

    private fun gotUiKeypress(event: KeyboardEvent) {
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
        sessionSelector.insertBefore(
            document.create.option { label = name; value = name },
            sessionSelector.childNodes.asList().find { (it as HTMLOptionElement).value > name }
        )
    }

    private fun resetCameraRotation() {
        cameraZRotation = 0f
        updateCameraRotation(0f)
    }

    private fun updateCameraRotation(angle: Float) {
        cameraZRotation += angle
        uiCamera.up.set(0, 1, 0)
        uiCamera.up.applyMatrix4(Matrix4().makeRotationZ(cameraZRotation.toDouble()))
    }

    private fun selectSurfacesMatching(pattern: String) {
        selectedSurfaces.forEach { it.deselect() }
        selectedSurfaces.clear()
        selectedSurfaces.addAll(modelSurfaceInfos.values.filter { it.name.contains(pattern, true) })
        selectedSurfaces.forEach { it.select() }
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

    override fun render(parentNode: HTMLElement) {
        parentNode.appendChild(screen)
        resizeTo(parentNode.offsetWidth, heightOrWindowHeight(parentNode))

        parentNode.onresize = {
            resizeTo(parentNode.offsetWidth, heightOrWindowHeight(parentNode))
        }
    }

    private fun heightOrWindowHeight(parentNode: HTMLElement): Int {
        return if (parentNode.offsetHeight == 0) window.innerHeight else parentNode.offsetHeight
    }

    override fun onClose() {
        statusListener?.mapperStatusChanged(false)

        listener.onClose()
    }

    private val diffCanvasScale = 1 / 3.0

    private fun resizeTo(width: Int, height: Int) {
        this.width = width
        this.height = height

        if (!haveCamDimensions) {
            camWidth = width
            camHeight = height
        }

        val scale = min(1f, min((width - 10).toFloat() / camWidth, (height - 10).toFloat() / camHeight))
        uiWidth = camWidth - 10
        uiHeight = camHeight - 10

        uiCamera.aspect = uiWidth.toDouble() / uiHeight
        uiCamera.updateProjectionMatrix()

        uiRenderer.setSize(uiWidth, uiHeight, true)
        ui3dCanvas.width = uiWidth
        ui3dCanvas.height = uiHeight

        ui2dCanvas.width = uiWidth
        ui2dCanvas.height = uiHeight
        ui2dCanvas.style.transform = "scale(${scale})"

        diffCanvas.width = (uiWidth * diffCanvasScale).toInt()
        diffCanvas.height = (uiHeight * diffCanvasScale).toInt()
        beforeCanvas.width = (uiWidth * diffCanvasScale).toInt()
        beforeCanvas.height = (uiHeight * diffCanvasScale).toInt()
        afterCanvas.width = (uiWidth * diffCanvasScale).toInt()
        afterCanvas.height = (uiHeight * diffCanvasScale).toInt()
    }

    override fun addWireframe(sheepModel: SheepModel) {
        val vertices = sheepModel.vertices.map { v -> Vector3(v.x, v.y, v.z) }.toTypedArray()
        sheepModel.panels.forEach { panel ->
            val geom = Geometry()
            val allFaces = mutableListOf<Face3>()
            geom.vertices = vertices

            val panelFaces = mutableListOf<Face3>()
            var faceNormalAcc = Vector3()
            panel.faces.faces.forEach { face ->
                val face3 = Face3(face.vertexIds[0], face.vertexIds[1], face.vertexIds[2], Vector3(0, 0, 0))
                allFaces.add(face3)
                panelFaces.add(face3)

                // just compute this face's normal
                geom.faces = arrayOf(face3)
                geom.computeFaceNormals()
                faceNormalAcc.add(face3.normal!!)
            }
            val surfaceNormal = faceNormalAcc.divideScalar(panel.faces.faces.size.toDouble())

            val panelMaterial = MeshBasicMaterial().apply { color = Color(0, 0, 0) }
            val mesh = Mesh(geom, panelMaterial)
            mesh.asDynamic().name = panel.name
            uiScene.add(mesh)

            val lineMaterial = LineBasicMaterial().apply {
                color = Color(0f, 1f, 0f)
                linewidth = 2.0
            }

            // offset the wireframe by one of the panel's face normals so it's not clipped by the panel mesh
            panel.lines.forEach { line ->
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

            geom.faces = allFaces.toTypedArray()
            geom.computeFaceNormals()
            geom.computeVertexNormals()

            modelSurfaceInfos[panel] = PanelInfo(panel.name, panelFaces, mesh, geom, lineMaterial)
        }

        uiScene.add(wireframe)

        val originMarker = Mesh(
            SphereBufferGeometry(1, 32, 32),
            MeshBasicMaterial().apply { color = Color(0xff0000) })
        uiScene.add(originMarker)

        val boundingBox = Box3().setFromObject(wireframe)
        uiControls.fitTo(boundingBox, false)
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
            val angle = panelInfo.faces[0].normal!!.dot(dirToCamera)
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
        private val points = Points().apply {
            geometry = this@VisibleSurface.geom
            material = this@VisibleSurface.material
        }
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
                for (i in 0..(pixels.keys.max()!!)) {
                    val position = pixels[i]?.positionInModel
                    vectors.add(position?.let {
                        Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat())
                    })
                }
                return vectors
            }

        private fun findIntersection(x: Float, y: Float): Intersect? {
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
            private val intersect: Intersect? by lazy { findIntersection(cameraX, cameraY) }
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
                matrix.fromArray(cameraMatrix.elements.toDoubleArray())
                // Get back position/rotation/scale attributes.
                matrix.asDynamic().decompose(position, quaternion, scale)
                updateMatrixWorld()
            }
        }

        companion object {
            fun from(camera: PerspectiveCamera): CameraOrientation {
                return CameraOrientation(
                    baaahs.geom.Matrix4(camera.matrix.toArray(js("undefined")).toTypedArray()),
                    camera.aspect
                )
            }
        }
    }

    override fun showCandidates(orderedPanels: List<Pair<MapperUi.VisibleSurface, Float>>) {
        orderedPanels as List<Pair<VisibleSurface, Float>>

        val firstGuess = orderedPanels.first()
        (firstGuess.first.panelInfo.mesh.material as MeshBasicMaterial).color.r += .25

        table.clear()
        table.append {
            table {
                tr {
                    th { +"Panel" }
                    th { +"Centroid dist" }
                }

                orderedPanels.subList(0, min(5, orderedPanels.size)).forEach { (visibleSurface, distance) ->
                    tr {
                        td { +visibleSurface.modelSurface.name }
                        td { +"$distance" }
                    }
                }
            }
        }
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
        messageDiv.innerText = message
        console.log("Message:", message)
    }

    override fun showMessage2(message: String) {
        message2Div.innerText = message
//        console.log("Message2:", message)
    }

    override fun showBefore(bitmap: Bitmap) {
        val beforeCanvas = document.body!!.first<HTMLCanvasElement>("mapperUi-before-canvas")
        val beforeCtx = beforeCanvas.getContext("2d") as CanvasRenderingContext2D
        beforeCtx.resetTransform()
        beforeCtx.scale(.3, .3)
        val renderBitmap: Any = when (bitmap) { // TODO: huh?
            is NativeBitmap -> bitmap.canvas
            is CanvasBitmap -> bitmap.canvas
            else -> bitmap
        }
        beforeCtx.drawImage(renderBitmap, 0.0, 0.0)
    }

    override fun showAfter(bitmap: Bitmap) {
        val afterCanvas = document.body!!.first<HTMLCanvasElement>("mapperUi-after-canvas")
        val afterCtx = afterCanvas.getContext("2d") as CanvasRenderingContext2D
        afterCtx.resetTransform()
        afterCtx.scale(.3, .3)
        val renderBitmap = when (bitmap) {
            is NativeBitmap -> bitmap.canvas
            is CanvasBitmap -> bitmap.canvas
            else -> bitmap
        }
        afterCtx.drawImage(renderBitmap, 0.0, 0.0)
    }

    override fun setRedo(fn: (suspend () -> Unit)?) {
        if (fn == null) {
            redoFn = null
        } else {
            redoFn = null
            redoButton.enabled(false)
            GlobalScope.launch { fn() }
        }
        redoButton.enabled(fn != null)
    }

    override fun showStats(total: Int, mapped: Int, visible: Int) {
        statsDiv.innerHTML = "<i class=\"fas fa-triangle\"></i>Mapped: $mapped / $total<br/>Visible: $visible"
    }

    override fun pauseForUserInteraction() {
        clickedPause()
    }

    private fun clickedPlay() {
        showPauseMode(false)
        listener.onStart()
    }

    private fun clickedPause() {
        showPauseMode(true)
        listener.onPause()
    }

    private fun showPauseMode(isPaused: Boolean) {
        pauseButton.enabled(!isPaused)
        playButton.enabled(isPaused)
    }

    private fun HTMLButtonElement.enabled(isEnabled: Boolean) {
        style.opacity = if (isEnabled) "1" else ".5"
    }

    private fun clickedStop() {
        listener.onStop()
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
    val mesh: Mesh,
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
        boundingBox.translate(mesh.getWorldPosition())
    }

    val boundingBox get() = _boundingBox.clone()

    private val rotator by lazy { Rotator(surfaceNormal, Vector3(0, 0, 1)) }

    fun toSurfaceNormal(point: Vector3): Vector3 {
        rotator.rotate(point); return point
    }

    private val normalBoundingBox: Box3 by lazy {
        val worldPos = mesh.getWorldPosition()
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

    val center get() = boundingBox.getCenter()

    val isMultiFaced get() = faces.size > 1

    val _surfaceNormal: Vector3 by lazy {
        val faceNormalSum = Vector3()
        var totalArea = 0f
        for (face in faces) {
            val triangle = Triangle(geom.vertices[face.a], geom.vertices[face.b], geom.vertices[face.c])
            val faceArea = triangle.asDynamic().getArea() as Float
            faceNormalSum.addScaledVector(face.normal!!, faceArea)
            totalArea += faceArea
        }
        faceNormalSum.divideScalar(totalArea.toDouble())
    }

    val surfaceNormal get() = _surfaceNormal.clone()

    var boxOnScreen: Box2? = null
}