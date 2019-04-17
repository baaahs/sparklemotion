package baaahs

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.external.controls.OrbitControls
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
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

actual class MapperDisplay actual constructor(val sheepModel: SheepModel, val onExit: () -> Unit) {
    private val width = 640
    private val height = 300

    private val domContainer = document.create.div("mapperUi") {
        button(classes = "ipad-close-button") {
            onClickFunction = { onExit.invoke() }
            +"X"
        }
        div("mapperUi-screen") {
            div("mapperUi-controls") {
                button(classes = "mapperUi-up") { +"▲"; onClickFunction = { wireframe.position.y += 10 } }
                button(classes = "mapperUi-down") { +"▼"; onClickFunction = { wireframe.position.y -= 10 } }
            }
            canvas(classes = "mapperUi-2d") {
                width = this@MapperDisplay.width.toString()
                height = this@MapperDisplay.height.toString()
            }
            div("mapperUi-3d") { }
        }
    }

    var uiRenderer: WebGLRenderer
    var uiScene = Scene()
    var uiCamera = PerspectiveCamera(45, width.toDouble() / height, 1, 10000)
    var uiControls: OrbitControls
    val wireframe = Object3D()

    val ui2dCanvas = domContainer.getElementsByClassName("mapperUi-2d")[0]!! as HTMLCanvasElement
    val ui2dCtx = ui2dCanvas.getContext("2d")!! as CanvasRenderingContext2D

    private var wireframeInitialized = false
    private var jsInitialized = false

    init {
        document.body!!.appendChild(domContainer)

        // onscreen renderer for registration UI:
        uiRenderer = WebGLRenderer(js("{alpha: true}"))
        uiRenderer.setSize(width, height);
        uiRenderer.setPixelRatio(width.toFloat() / height);
        domContainer.getElementsByClassName("mapperUi-3d")[0]!!.appendChild(uiRenderer.domElement);

        uiCamera.position.z = 1000.0
        uiScene.add(uiCamera)

        uiControls = OrbitControls(uiCamera, uiRenderer.domElement)
        addWireframe()
    }

    private fun addWireframe() {
        val geom = Geometry()
        val lineMaterial = LineBasicMaterial()
        lineMaterial.color = Color(0f, 1f, 0f)

        geom.vertices = sheepModel.vertices.map { v -> Vector3(v.x, v.y, v.z) }.toTypedArray()
        val faces = mutableListOf<Face3>()
        sheepModel.panels.forEach { panel ->
            panel.faces.faces.forEach { face ->
                faces.add(Face3(face.vertexIds[0], face.vertexIds[1], face.vertexIds[2], Vector3(0, 0, 0)))
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

    @ExperimentalUnsignedTypes
    actual fun showCamImage(image: MediaDevices.Image) {
        ui2dCtx.resetTransform()
        ui2dCtx.fillStyle = "#006600"
        ui2dCtx.fillRect(0.0, 0.0, width / 2.0, height / 2.0);
        val imageDataImage = image as ImageDataImage
        val imageData = imageDataImage.imageData
        window.createImageBitmap(imageData).then { imageBitmap: ImageBitmap ->
            if (imageDataImage.rowsReversed) {
                ui2dCtx.translate(0.0, height.toDouble())
                ui2dCtx.scale(1.0, -1.0)
                ui2dCtx.drawImage(imageBitmap, 0.0, 0.0 /*height.toDouble()*/)
            } else {
                ui2dCtx.drawImage(imageBitmap, 0.0, 0.0)
            }
        }

        uiRenderer.render(uiScene, uiCamera)
    }
}

@ExperimentalUnsignedTypes
class ImageDataImage(val imageData: ImageData, val rowsReversed: Boolean = false) : MediaDevices.Image {
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
