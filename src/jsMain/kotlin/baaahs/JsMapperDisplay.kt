package baaahs

import baaahs.imaging.Bitmap
import baaahs.imaging.CanvasBitmap
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.*
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.plus
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import kotlinx.html.button
import kotlinx.html.canvas
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.i
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.math.max
import kotlin.math.roundToInt

class JsMapperDisplay(container: DomContainer) : MapperDisplay {
    private lateinit var listener: MapperDisplay.Listener

    override fun listen(listener: MapperDisplay.Listener) {
        this.listener = listener
    }

    private var width = 512
    private var height = 384

    val clock = Clock();

    // onscreen renderer for registration UI:
    val uiRenderer = WebGLRenderer(js("{alpha: true}"))
    var uiScene = Scene()
    var uiCamera = PerspectiveCamera(45, width.toDouble() / height, 1, 10000)
    var uiControls: dynamic
    val wireframe = Object3D()

    private val screen = document.create.div("mapperUi-screen") {
        div("mapperUi-controls") {
            button { +"▲"; onClickFunction = { wireframe.position.y += 10 } }
            button { +"▼"; onClickFunction = { wireframe.position.y -= 10 } }
//            button { i(classes="fas fa-crosshairs"); onClickFunction = { target() } }
            button { i(classes="fas fa-play"); onClickFunction = { go() } }
            button { i(classes="fas fa-pause"); onClickFunction = { listener.onPause() } }
            button { i(classes="fas fa-stop"); onClickFunction = { listener.onStop() }; disabled = true }
        }
        canvas(classes = "mapperUi-2d-canvas") {
            width = this@JsMapperDisplay.width.toString() + "px"
            height = this@JsMapperDisplay.height.toString() + "px"
        }
        div("mapperUi-3d-div") { }
        canvas(classes = "mapperUi-diff-canvas") {
            width = this@JsMapperDisplay.width.toString() + "px"
            height = this@JsMapperDisplay.height.toString() + "px"
        }
        div("mapperUi-stats") { }
        div("mapperUi-message") { }
        div("mapperUi-message2") { }
    }

    private val frame = container.getFrame(
        "Mapper",
        screen,
        { listener.onClose() },
        { width, height -> this.resizeTo(width, height) })

    private val ui2dCanvas = screen.first<HTMLCanvasElement>("mapperUi-2d-canvas")
    private val ui2dCtx = ui2dCanvas.context2d()

    private val ui3dDiv = screen.first<HTMLCanvasElement>("mapperUi-3d-div")
    private val ui3dCanvas = uiRenderer.domElement as HTMLCanvasElement

    private val diffCanvas = screen.first<HTMLCanvasElement>("mapperUi-diff-canvas")
    private val diffCtx = diffCanvas.context2d()
    private var changeRegion: MediaDevices.Region? = null

    private val statsDiv = screen.first<HTMLDivElement>("mapperUi-stats")
    private val messageDiv = screen.first<HTMLDivElement>("mapperUi-message")
    private val message2Div = screen.first<HTMLDivElement>("mapperUi-message2")

    val visiblePanels = mutableListOf<Pair<SheepModel.Panel, PanelInfo>>()

    private fun resizeTo(width: Int, height: Int) {
        this.width = width
        this.height = height

        uiCamera.aspect = width.toDouble() / height
        uiCamera.updateProjectionMatrix()

        uiRenderer.setSize(width, height);
        uiRenderer.setPixelRatio(width.toFloat() / height);
        (uiRenderer.domElement as HTMLCanvasElement).width = width
        (uiRenderer.domElement as HTMLCanvasElement).height = height

        ui2dCanvas.width = width
        ui2dCanvas.height = height

        diffCanvas.width = width
        diffCanvas.height = height
    }

    private val panelInfos = mutableMapOf<SheepModel.Panel, PanelInfo>()

    init {
        js("document.md = this");
        ui3dDiv.appendChild(ui3dCanvas);

        uiCamera.position.z = 1000.0
        uiScene.add(uiCamera)

        uiControls = js("document.createCameraControls")(uiCamera, uiRenderer.domElement)
    }

    override fun addWireframe(sheepModel: SheepModel) {
        val geom = Geometry()
        val lineMaterial = LineBasicMaterial().apply { color = Color(0f, 1f, 0f) }
        val panelMaterial = MeshBasicMaterial().apply { color = Color(0, 0, 0) }

        geom.vertices = sheepModel.vertices.map { v -> Vector3(v.x, v.y, v.z) }.toTypedArray()
        val allFaces = mutableListOf<Face3>()
        sheepModel.panels.forEach { panel ->
            val panelFaces = mutableListOf<Face3>()
            var faceNormal = Vector3()
            panel.faces.faces.forEach { face ->
                val face3 = Face3(face.vertexIds[0], face.vertexIds[1], face.vertexIds[2], Vector3(0, 0, 0))
                allFaces.add(face3)
                panelFaces.add(face3)

                // just compute this face's normal
                geom.faces = arrayOf(face3)
                geom.computeFaceNormals()
                faceNormal = face3.normal!!
            }

            val mesh = Mesh(geom, panelMaterial)
            uiScene.add(mesh)

            // offset the wireframe by one of the panel's face normals so it's not clipped by the panel mesh
            panel.lines.forEach { line ->
                val lineGeom = BufferGeometry()
                lineGeom.setFromPoints(line.points.map { pt -> Vector3(pt.x, pt.y, pt.z) + faceNormal }.toTypedArray())
                wireframe.add(Line(lineGeom, lineMaterial))
            }

            panelInfos[panel] = PanelInfo(panelFaces, mesh, geom)
        }
        geom.faces = allFaces.toTypedArray()
        geom.computeFaceNormals()
        geom.computeVertexNormals()
        geom.computeBoundingSphere()

        uiScene.add(wireframe)

        val originMarker = Mesh(
            SphereBufferGeometry(1, 32, 32),
            MeshBasicMaterial().apply { color = Color(0xff0000) });
        uiScene.add(originMarker);

        val boundingSphere: dynamic = geom.boundingSphere!!
        val centerOfSheep = boundingSphere.center.clone()

        uiCamera.lookAt(centerOfSheep)
    }

    override fun getCandidateSurfaces(changeRegion: MediaDevices.Region): List<SheepModel.Panel> {
        val panelRects = visiblePanels.associate { (panel, panelInfo) ->
            panelInfo.mesh.updateMatrixWorld()

            val panelBasePosition = panelInfo.mesh.position

            var minX = Int.MAX_VALUE
            var maxX = Int.MIN_VALUE
            var minY = Int.MAX_VALUE
            var maxY = Int.MIN_VALUE

            val widthHalf = width / 2.0
            val heightHalf = height / 2.0

            for (face in panelInfo.faces) {
                for (vertexI in arrayOf(face.a, face.b, face.c)) {
                    val v = panelBasePosition.clone() + panelInfo.geom.vertices[vertexI]
                    v.project(uiCamera)

                    val x = ((v.x * widthHalf) + widthHalf).toInt();
                    val y = (-(v.y * heightHalf) + heightHalf).toInt();

                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }

            Pair(panel, MediaDevices.Region(minX, minY, maxX, maxY))
        }

        val orderedPanels = visiblePanels
            .sortedBy { (panel, _) -> panelRects[panel]!!.distanceTo(changeRegion) }
            .map { (panel, _) -> panel }
        return orderedPanels
    }

    override fun showCamImage(image: baaahs.imaging.Image) {
        ui2dCtx.resetTransform()

        val scale = max(
            width.toDouble() / image.width,
            height.toDouble() / image.height
        )
        val imgWidth = (image.width * scale).roundToInt()
        val imgHeight = (image.height * scale).roundToInt()

        val widthDiff = width - imgWidth
        val heightDiff = height - imgHeight

        val widthOff = widthDiff / 2.0
        val heightOff = heightDiff / 2.0

        CanvasBitmap(ui2dCanvas).drawImage(image,
            0, 0, image.width, image.height,
            widthDiff / 2, heightDiff / 2, imgWidth, imgHeight
        )

        // add a green line around the camera image:
        ui2dCtx.strokeStyle = "#006600"
        ui2dCtx.strokeRect(widthOff, heightOff, imgWidth.toDouble(), imgHeight.toDouble())

        changeRegion?.apply {
            ui2dCtx.strokeStyle = "#ff0000"
            ui2dCtx.strokeRect(x0 * scale + widthOff, y0 * scale + heightOff, width * scale, height * scale)
        }

        uiControls.update(clock.getDelta())
        uiRenderer.render(uiScene, uiCamera)
    }

    override fun showDiffImage(deltaBitmap: Bitmap, changeRegion: MediaDevices.Region) {
        this.changeRegion = changeRegion

        CanvasBitmap(diffCanvas).drawImage(deltaBitmap.asImage())
        diffCtx.strokeStyle = "#ff0000"
        changeRegion.apply {
            diffCtx.strokeRect(x0.toDouble(), y0.toDouble(),
                changeRegion.width.toDouble(), changeRegion.height.toDouble())
        }
    }

    override fun showMessage(message: String) {
        messageDiv.innerText = message
    }

    override fun showMessage2(message: String) {
        message2Div.innerText = message
    }

    override fun showStats(total: Int, mapped: Int, visible: Int) {
        statsDiv.innerHTML = "<i class=\"fas fa-triangle\"></i>Mapped: $mapped / $total<br/>Visible: $visible"
    }

    private fun go() {
        listener.onStart()

        computeVisiblePanels()
    }

    private fun computeVisiblePanels() {
        visiblePanels.clear()

        panelInfos.forEach { (panel, panelInfo) ->
            val panelPosition = panelInfo.geom.vertices[panelInfo.faces[0].a]
            val dirToCamera = uiCamera.position.clone().sub(panelPosition)
            dirToCamera.normalize()
            val angle = panelInfo.faces[0].normal!!.dot(dirToCamera)
            println("Angle for ${panel.name} is $angle")
            if (angle > 0) {
                visiblePanels.add(Pair(panel, panelInfo))
            }
        }
    }

    override fun close() {
        frame.close()
    }
}

class PanelInfo(val faces: MutableList<Face3>, val mesh: Mesh, val geom: Geometry)