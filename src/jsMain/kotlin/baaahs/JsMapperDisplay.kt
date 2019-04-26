package baaahs

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.external.controls.OrbitControls
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import kotlinx.html.button
import kotlinx.html.canvas
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import org.khronos.webgl.get
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class JsMapperDisplay(container: DomContainer) : MapperDisplay {
    override var onClose: () -> Unit = {}

    private var width = 640
    private var height = 300

    // onscreen renderer for registration UI:
    val uiRenderer = WebGLRenderer(js("{alpha: true}"))
    var uiScene = Scene()
    var uiCamera = PerspectiveCamera(45, width.toDouble() / height, 1, 10000)
    var uiControls: OrbitControls
    val wireframe = Object3D()

    private val screen = document.create.div("mapperUi-screen") {
        div("mapperUi-controls") {
            button(classes = "mapperUi-up") { +"▲"; onClickFunction = { wireframe.position.y += 10 } }
            button(classes = "mapperUi-down") { +"▼"; onClickFunction = { wireframe.position.y -= 10 } }
        }
        canvas(classes = "mapperUi-2d-canvas") {
            width = this@JsMapperDisplay.width.toString() + "px"
            height = this@JsMapperDisplay.height.toString() + "px"
        }
        div("mapperUi-3d-div") { }
    }

    private val frame = container.getFrame(
        "Mapper",
        screen,
        { this.onClose() },
        { width, height -> this.resizeTo(width, height) })

    val ui2dCanvas = screen.getElementsByClassName("mapperUi-2d-canvas")[0]!! as HTMLCanvasElement
    val ui2dCtx = ui2dCanvas.getContext("2d")!! as CanvasRenderingContext2D
    val ui3dDiv = screen.getElementsByClassName("mapperUi-3d-div")[0]!! as HTMLDivElement
    val ui3dCanvas = uiRenderer.domElement as HTMLCanvasElement

    private fun resizeTo(width: Int, height: Int) {
        this.width = width
        this.height = height

        uiCamera.aspect = width.toDouble() / height

        uiRenderer.setSize(width, height);
        uiRenderer.setPixelRatio(width.toFloat() / height);
        (uiRenderer.domElement as HTMLCanvasElement).width = width
        (uiRenderer.domElement as HTMLCanvasElement).height = height

        ui2dCanvas.width = width
        ui2dCanvas.height = height
    }


    private var wireframeInitialized = false
    private var jsInitialized = false

    init {
        ui3dDiv.appendChild(ui3dCanvas);

        uiCamera.position.z = 1000.0
        uiScene.add(uiCamera)

        uiControls = OrbitControls(uiCamera, uiRenderer.domElement)
        uiControls.minPolarAngle = PI / 2 - .25; // radians
        uiControls.maxPolarAngle = PI / 2 + .25; // radians
    }

    override fun addWireframe(sheepModel: SheepModel) {
        val geom = Geometry()
        val lineMaterial = LineBasicMaterial().apply { color = Color(0f, 1f, 0f) }
        val panelMaterial = MeshBasicMaterial().apply { color = Color(0, 0, 0) }

        geom.vertices = sheepModel.vertices.map { v -> Vector3(v.x, v.y, v.z) }.toTypedArray()
        val faces = mutableListOf<Face3>()
        sheepModel.panels.forEach { panel ->
            panel.faces.faces.forEach { face ->
                val face3 = Face3(face.vertexIds[0], face.vertexIds[1], face.vertexIds[2], Vector3(0, 0, 0))
                faces.add(face3)
                val mesh = Mesh(geom, panelMaterial)
                uiScene.add(mesh)
            }

            panel.lines.forEach { line ->
                val lineGeom = BufferGeometry()
                lineGeom.setFromPoints(line.points.map { pt -> Vector3(pt.x, pt.y, pt.z) }.toTypedArray())
                wireframe.add(Line(lineGeom, lineMaterial))
            }
        }
        geom.faces = faces.toTypedArray()
        geom.computeVertexNormals()
        geom.computeBoundingSphere()

        uiScene.add(wireframe)
        val boundingSphere: dynamic = geom.boundingSphere!!
        uiControls.target = boundingSphere.center
        uiControls.update()
    }

    override fun showCamImage(image: MediaDevices.Image) {
        ui2dCtx.resetTransform()

        val imageDataImage = image as ImageDataImage
        val imageData = imageDataImage.imageData
        val options = ImageBitmapOptions()
        if (image.rowsReversed) {
            options.imageOrientation = ImageOrientation.Companion.FLIPY
        }
        window.createImageBitmap(imageData, options).then { imageBitmap: ImageBitmap ->
            val scale = min(
                width.toDouble() / imageData.width,
                height.toDouble() / imageData.height
            )
            val imgWidth = (imageData.width * scale).roundToInt()
            val imgHeight = (imageData.height * scale).roundToInt()

            val widthDiff = width - imgWidth
            val heightDiff = height - imgHeight

            ui2dCtx.drawImage(
                imageBitmap, 0.0, 0.0, imageBitmap.width.toDouble(), imageBitmap.height.toDouble(),
                widthDiff / 2.0, heightDiff / 2.0, width - widthDiff / 2.0, height - heightDiff / 2.0
            )

            // add a green line around the camera image:
            ui2dCtx.strokeStyle = "#006600"
            ui2dCtx.strokeRect(
                widthDiff / 2.0, heightDiff / 2.0,
                width - widthDiff / 2.0, height - heightDiff / 2.0
            );
        }

        uiRenderer.render(uiScene, uiCamera)
    }

    override fun close() {
        frame.close()
    }
}

class ImageDataImage(val imageData: ImageData, val rowsReversed: Boolean = false) : MediaDevices.Image {
    @ExperimentalUnsignedTypes
    override fun toMonoBitmap(): MediaDevices.MonoBitmap {
        val destBuf = UByteArray(imageData.width * imageData.height)
        val srcBuf = imageData.data

        val srcBytesPerPixel = 4
        val srcBytesPerRow = imageData.width * srcBytesPerPixel
        val destBytesPerPixel = 1
        val destBytesPerRow = imageData.width * destBytesPerPixel
        val greenOffset = 1 // RGBA

        for (row in 0 until imageData.height) {
            for (col in 0 until imageData.width) {
                val srcRow = if (rowsReversed) imageData.height - row else row
                destBuf[row * destBytesPerRow + col * destBytesPerPixel] =
                    srcBuf[srcRow * srcBytesPerRow + col * srcBytesPerPixel + greenOffset].toUByte()
            }
        }
        return MediaDevices.MonoBitmap(imageData.width, imageData.height, destBuf)
    }
}
