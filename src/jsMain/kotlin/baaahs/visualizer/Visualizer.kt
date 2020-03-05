package baaahs.visualizer

import baaahs.*
import baaahs.dmx.LixadaMiniMovingHead
import baaahs.sim.FakeDmxUniverse
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.geometries.ConeBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.MouseEvent
import three.Matrix4
import three.OrbitControls
import kotlin.browser.document
import kotlin.browser.window
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Visualizer(
    model: Model<*>,
    private val display: VisualizerDisplay,
    private val container: HTMLDivElement,
    private val selectionInfo: HTMLDivElement? = null,
    private val rotationCheckbox: HTMLInputElement? = null
): JsMapperUi.StatusListener {

    var stopRendering: Boolean = false

    private var rotate: Boolean
        get() = rotationCheckbox?.checked ?: false
        set(value) {
            rotationCheckbox?.checked = value
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

    private val raycaster: three.Raycaster
    private var mouse: Vector2? = null
    private val sphere: Mesh

    private val rendererListeners = mutableListOf<() -> Unit>()

    private var vizPanels = mutableListOf<VizSurface>()

    init {
        container.addEventListener("mousedown", { event -> onMouseDown(event as MouseEvent) }, false)
        camera = PerspectiveCamera(45, container.offsetWidth.toDouble() / container.offsetHeight, 1, 10000)
        camera.position.z = 1000.0
        controls = OrbitControls(camera, container)
        controls.minPolarAngle = PI / 2 - .25 // radians
        controls.maxPolarAngle = PI / 2 + .25 // radians

        scene = Scene()
        pointMaterial = PointsMaterial().apply { color.set(0xffffff) }
        lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
        panelMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa); linewidth = 3.0 }
        scene.add(camera)
        renderer = WebGLRenderer()
        renderer.setPixelRatio(window.devicePixelRatio)
        renderer.setSize(container.offsetWidth, container.offsetHeight)
        container.addEventListener("resize", { doResize() })

        container.appendChild(renderer.domElement)
        geom = Geometry()
        raycaster = three.Raycaster()
        raycaster.asDynamic().params.Points.threshold = 1
        sphere = Mesh(
            SphereBufferGeometry(1, 32, 32),
            MeshBasicMaterial().apply { color.set(0xff0000) }
        )
        scene.add(sphere)

        // convert from SheepModel to THREE
        model.geomVertices.forEach { v ->
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
            (event.clientX.toDouble() / container.offsetWidth) * 2 - 1,
            -(event.clientY.toDouble() / container.offsetHeight) * 2 + 1
        )
    }

    fun addSurface(p: Model.Surface): VizSurface {
        // if (p.name !== '15R') return
        // if (omitPanels.includes(p.name)) return

        val vizPanel = VizSurface(p, geom, scene)
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
        private val baseXRotation = PI
        private val baseYRotation = 0.0
        private val baseZRotation = 0.0

        init {
            geometry.applyMatrix(Matrix4().makeTranslation(0.0, -500.0, 0.0))
            material.transparent = true
            material.opacity = .75
            cone.position.set(movingHead.origin.x, movingHead.origin.y, movingHead.origin.z)
            cone.rotation.x = baseXRotation
            cone.rotation.y = baseYRotation
            cone.rotation.z = baseZRotation
            scene.add(cone)
        }

        private fun receivedDmxFrame() {
            material.color.set(device.color.rgb)
            material.visible = device.dimmer > .1

            cone.rotation.x = baseXRotation + device.tilt
            cone.rotation.y = baseYRotation
            cone.rotation.z = baseZRotation
        }
    }

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
        if (stopRendering) return

        window.setTimeout(fun() {
            window.requestAnimationFrame { render() }
        }, REFRESH_DELAY)

        mouse?.let { mouseClick ->
            mouse = null
            raycaster.setFromCamera(mouseClick, camera)
            val intersections = raycaster.intersectObjects(scene.children, false)
            intersections.forEach { intersection ->
                val intersectedObject = intersection.`object`
                val vizPanel = VizSurface.getFromObject(intersectedObject)
                vizPanel?.let {
                    selectionInfo?.innerText = "Selected: " + vizPanel.name
                    return@forEach
                }
            }
        }

        if (!mapperIsRunning) {
            if (rotationCheckbox?.checked == true) {
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
        camera.aspect = container.offsetWidth.toDouble() / container.offsetHeight
        camera.updateProjectionMatrix()
        renderer.setSize(container.offsetWidth, container.offsetHeight)
    }

    override fun mapperStatusChanged(isRunning: Boolean) {
        mapperIsRunning = isRunning
    }

    interface FrameListener {
        @JsName("onFrameReady")
        fun onFrameReady(scene: Scene, camera: Camera)
    }
}
