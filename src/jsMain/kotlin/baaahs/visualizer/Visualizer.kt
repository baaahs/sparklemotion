package baaahs.visualizer

import baaahs.Config
import baaahs.JsMapperUi
import baaahs.dmx.LixadaMiniMovingHead
import baaahs.getTimeMillis
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Framerate
import baaahs.window
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.geometries.ConeBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.WebGLRenderer
import info.laht.threekt.scenes.Scene
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent
import three.Matrix4
import three.OrbitControls
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Visualizer(model: Model): JsMapperUi.StatusListener {
    val facade = Facade()

    private var container: HTMLDivElement? = null
        set(value) {
            if (value != null) {
                field = value
                containerAttached()
            } else {
                containerWillDetach()
                field = value
            }
        }

    var stopRendering: Boolean = false
    var rotate: Boolean = false

    var mapperIsRunning = false
        set(isRunning) {
            field = isRunning

            vizPanels.forEach { panel -> panel.faceMaterial.transparent = !isRunning }

            if (isRunning) {
                rotate = false
            }
        }

    private val frameListeners = mutableListOf<FrameListener>()

    private var controls: OrbitControls? = null
    private val camera: PerspectiveCamera =
        PerspectiveCamera(45, 1.0, 1, 10000).apply {
            position.z = 1000.0
        }
    private val scene: Scene = Scene()
    private val renderer = WebGLRenderer()
    private val geom = Geometry()

    private var obj: Object3D = Object3D()
    private val pointMaterial = PointsMaterial().apply { color.set(0xffffff) }

    private val raycaster = three.Raycaster()
    private var mouse: Vector2? = null
    private val sphere: Mesh

    private val rendererListeners = mutableListOf<() -> Unit>()

    private var vizPanels = mutableListOf<VizSurface>()

    init {
        scene.add(camera)
//        renderer.setPixelRatio(window.devicePixelRatio)

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

        var resizeTaskId: Int? = null
        window.addEventListener("resize", {
            if (resizeTaskId !== null) {
                window.clearTimeout(resizeTaskId!!)
            }

            resizeTaskId = window.setTimeout({
                resizeTaskId = null
                resize()
            }, resizeDelay)
        })
    }

    private fun containerAttached() {
        container!!.appendChild(renderer.domElement)

        controls = OrbitControls(camera, container!!).apply {
            minPolarAngle = PI / 2 - .25 // radians
            maxPolarAngle = PI / 2 + .25 // radians

            enableKeys = false
        }

        resize()
        startRender()
    }

    private fun containerWillDetach() {
        container?.removeChild(renderer.domElement)
        stopRendering = true
        controls = null
    }

    fun addFrameListener(frameListener: FrameListener) {
        frameListeners.add(frameListener)
    }

    fun removeFrameListener(frameListener: FrameListener) {
        frameListeners.remove(frameListener)
    }

    fun onMouseDown(event: MouseEvent) {
        container?.let {
            mouse = Vector2(
                (event.clientX.toDouble() / it.offsetWidth) * 2 - 1,
                -(event.clientY.toDouble() / it.offsetHeight) * 2 + 1
            )
        }
    }

    fun addSurface(surfaceGeometry: SurfaceGeometry): VizSurface {
        // if (p.name !== '15R') return
        // if (omitPanels.includes(p.name)) return

        val vizPanel = VizSurface(surfaceGeometry, scene)
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
        controls?.target = target
        camera.lookAt(target)

        stopRendering = false
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
                VizSurface.getFromObject(intersectedObject)?.let {
                    facade.selectedSurface = it
                    facade.notifyChanged()
                    return@forEach
                }
            }
        }

        if (!mapperIsRunning && rotate) {
            val rotSpeed = .01
            val x = camera.position.x
            val z = camera.position.z
            camera.position.x = x * cos(rotSpeed) + z * sin(rotSpeed)
            camera.position.z = z * cos(rotSpeed * 2) - x * sin(rotSpeed * 2)
            camera.lookAt(scene.position)
        }

        controls?.update()

        val startMs = getTimeMillis()
        renderer.render(scene, camera)
        facade.framerate.elapsed((getTimeMillis() - startMs).toInt())

        frameListeners.forEach { f -> f.onFrameReady(scene, camera) }
        rendererListeners.forEach { value -> value() }
    }

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

    private val resizeDelay = 100

    fun resize() {
        container?.let {
            val canvas = renderer.domElement as HTMLCanvasElement
            canvas.width = it.offsetWidth
            canvas.height = it.offsetHeight

            camera.aspect = it.offsetWidth.toDouble() / it.offsetHeight
            camera.updateProjectionMatrix()

            renderer.setSize(it.offsetWidth, it.offsetHeight, updateStyle = false)
        }
    }

    override fun mapperStatusChanged(isRunning: Boolean) {
        mapperIsRunning = isRunning
    }

    interface FrameListener {
        @JsName("onFrameReady")
        fun onFrameReady(scene: Scene, camera: Camera)
    }

    inner class Facade : baaahs.ui.Facade() {
        var container: HTMLDivElement?
            get() = this@Visualizer.container
            set(value) { this@Visualizer.container = value }

        var rotate: Boolean
            get() = this@Visualizer.rotate
            set(value) { this@Visualizer.rotate = value}

        var selectedSurface: VizSurface? = null

        val framerate = Framerate()

        fun resize() = this@Visualizer.resize()
        fun onMouseDown(event: MouseEvent) = this@Visualizer.onMouseDown(event)
    }
}
