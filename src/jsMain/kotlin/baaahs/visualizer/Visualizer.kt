package baaahs.visualizer

import baaahs.*
import baaahs.dmx.LixadaMiniMovingHead
import baaahs.dmx.Shenzarpy
import baaahs.sim.FakeDmxUniverse
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.external.controls.OrbitControls
import info.laht.threekt.geometries.ConeBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Visualizer(sheepModel: SheepModel, private val display: VisualizerDisplay): JsMapperUi.StatusListener {

    private var rotate: Boolean
        get() = getVizRotationEl().checked
        set(value) {
            getVizRotationEl().checked = value
        }

    var mapperIsRunning = false
        set(isRunning) {
            field = isRunning

            vizPanels.forEach { panel -> panel.faceMaterial.transparent = !isRunning }

            if (isRunning) {
                rotate = false
            }
        }

    private val frameListeners = mutableListOf<FrameListener>()

    private val controls: OrbitControls
    private val camera: PerspectiveCamera
    private val scene: Scene
    private val renderer: WebGLRenderer
    private val geom: Geometry

    private var obj: Object3D = Object3D()
    private val pointMaterial: Material
    private val lineMaterial: Material
    private val panelMaterial: Material

    private val raycaster: Raycaster
    private var mouse: Vector2? = null
    private val sphere: Mesh

    private val rendererListeners = mutableListOf<() -> Unit>()

    private var vizPanels = mutableListOf<VizPanel>()

    private var sheepView = document.getElementById("sheepView")!! as HTMLDivElement
    private val selectionInfo = document.getElementById("selectionInfo")!! as HTMLDivElement

    init {
        sheepView.addEventListener("mousedown", { event -> onMouseDown(event as MouseEvent) }, false)
        camera = PerspectiveCamera(45, sheepView.offsetWidth.toDouble() / sheepView.offsetHeight, 1, 10000)
        camera.position.z = 1000.0
        controls = OrbitControls(camera, sheepView)
        controls.minPolarAngle = PI / 2 - .25 // radians
        controls.maxPolarAngle = PI / 2 + .25 // radians

        scene = Scene()
        pointMaterial = PointsMaterial().apply { color.set(0xffffff) }
        lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
        panelMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa); linewidth = 3.0 }
        scene.add(camera)
        renderer = WebGLRenderer()
        renderer.setPixelRatio(window.devicePixelRatio)
        renderer.setSize(sheepView.offsetWidth, sheepView.offsetHeight)
        sheepView.appendChild(renderer.domElement)
        geom = Geometry()
        raycaster = Raycaster()
        raycaster.asDynamic().params.Points.threshold = 1
        sphere = Mesh(
            SphereBufferGeometry(1, 32, 32),
            MeshBasicMaterial().apply { color.set(0xff0000) }
        )
        scene.add(sphere)

        // convert from SheepModel to THREE
        sheepModel.vertices.forEach { v ->
            geom.vertices.asDynamic().push(Vector3(v.x, v.y, v.z))
        }

        startRender()

        var resizeTaskId: Int? = null
        window.addEventListener("resize", {
            if (resizeTaskId !== null) {
                window.clearTimeout(resizeTaskId!!)
            }

            resizeTaskId = window.setTimeout({
                resizeTaskId = null
                doResize()
            }, resizeDelay)
        })
    }

    fun addFrameListener(frameListener: FrameListener) {
        frameListeners.add(frameListener)
    }

    fun removeFrameListener(frameListener: FrameListener) {
        frameListeners.remove(frameListener)
    }

    fun onMouseDown(event: MouseEvent) {
        mouse = Vector2(
            (event.clientX.toDouble() / sheepView.offsetWidth) * 2 - 1,
            -(event.clientY.toDouble() / sheepView.offsetHeight) * 2 + 1
        )
    }

    fun addPanel(p: SheepModel.Panel): VizPanel {
        // if (p.name !== '15R') return
        // if (omitPanels.includes(p.name)) return

        val vizPanel = VizPanel(p, geom, scene)
        vizPanels.add(vizPanel)
        return vizPanel
    }

    fun addMovingHead(movingHead: MovingHead, dmxUniverse: FakeDmxUniverse): VizMovingHead {
        return VizMovingHead(movingHead, dmxUniverse)
    }

    inner class VizMovingHead(movingHead: MovingHead, dmxUniverse: FakeDmxUniverse) {
        private val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
        private val device = LixadaMiniMovingHead(dmxUniverse.reader(baseChannel, 16) { receivedDmxFrame() })
        private val geometry = ConeBufferGeometry(50, 1000)
        private val material = MeshBasicMaterial().apply { color.set(0xffff00) }
        private val cone = Mesh(geometry, material)


        init {
            geometry.applyMatrix(Matrix4().makeTranslation(0.0, -500.0, 0.0))
            material.transparent = true
            material.opacity = .75
            cone.position.set(movingHead.origin.x, movingHead.origin.y, movingHead.origin.z)
            cone.rotation.x = -PI / 2
            scene.add(cone)
        }

        private fun receivedDmxFrame() {
            material.color.set(device.color.rgb)
            material.visible = device.dimmer > .1

            cone.rotation.x = -PI / 2 + device.tilt
            cone.rotation.z = device.pan.toDouble()
        }
    }

    private fun getVizRotationEl() = document.getElementById("vizRotation") as HTMLInputElement

    private fun startRender() {
        geom.computeBoundingSphere()
        this.obj = Points().apply { geometry = geom; material = pointMaterial }
        scene.add(obj)
        val target = geom.boundingSphere.asDynamic().center.clone()
        controls.target = target
        camera.lookAt(target)

        render()
    }

    private val REFRESH_DELAY = 50 // ms

    fun render() {
        window.setTimeout(fun() {
            window.requestAnimationFrame { render() }
        }, REFRESH_DELAY)

        mouse?.let { mouseClick ->
            mouse = null
            raycaster.setFromCamera(mouseClick, camera)
            val intersections = raycaster.intersectObjects(scene.children, false)
            intersections.forEach { intersection ->
                val intersectedObject = intersection.`object`
                val vizPanel = VizPanel.getFromObject(intersectedObject)
                vizPanel?.let {
                    selectionInfo.innerText = "Selected: " + vizPanel.name
                    return@forEach
                }
            }
        }

        if (!mapperIsRunning) {
            if (getVizRotationEl().checked) {
                val rotSpeed = .01
                val x = camera.position.x
                val z = camera.position.z
                camera.position.x = x * cos(rotSpeed) + z * sin(rotSpeed)
                camera.position.z = z * cos(rotSpeed * 2) - x * sin(rotSpeed * 2)
                camera.lookAt(scene.position)
            }
        }

        controls.update()

        val startMs = getTimeMillis()
        renderer.render(scene, camera)
        display.renderMs = (getTimeMillis() - startMs).toInt()

        frameListeners.forEach { f -> f.onFrameReady(scene, camera) }
        rendererListeners.forEach { value -> value() }
    }

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

    private val resizeDelay = 100

    private fun doResize() {
        camera.aspect = sheepView.offsetWidth.toDouble() / sheepView.offsetHeight
        camera.updateProjectionMatrix()
        renderer.setSize(sheepView.offsetWidth, sheepView.offsetHeight)
    }

    override fun mapperStatusChanged(isRunning: Boolean) {
        mapperIsRunning = isRunning
    }

    interface FrameListener {
        @JsName("onFrameReady")
        fun onFrameReady(scene: Scene, camera: Camera)
    }
}
