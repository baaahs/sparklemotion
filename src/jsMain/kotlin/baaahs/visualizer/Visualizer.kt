package baaahs.visualizer

import baaahs.*
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.util.Framerate
import baaahs.util.asMillis
import baaahs.visualizer.movers.VizMovingHead
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.MouseEvent
import three.js.*
import three_ext.OrbitControls
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Visualizer(model: Model, private val clock: Clock) : JsMapperUi.StatusListener {
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

    private val preRenderListeners = mutableListOf<() -> Unit>()
    private val frameListeners = mutableListOf<FrameListener>()

    private var controls: OrbitControls? = null
    private val camera: PerspectiveCamera =
        PerspectiveCamera(45, 1.0, 1, 10000).apply {
            position.z = 1000.0
        }
    private val scene: Scene = Scene()
    private val renderer = WebGLRenderer().apply {
        localClippingEnabled = true
    }
    private val geom = Geometry()

    private var obj: Object3D = Object3D()
    private val pointMaterial = PointsMaterial().apply { color.set(0xffffff) }

    private val raycaster = Raycaster()
    private var mouse: Vector2? = null
    private val sphere: Mesh<*, *>

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

    fun addPreRenderListener(listener: () -> Unit) {
        preRenderListeners.add(listener)
    }

    fun removePreRenderListener(listener: () -> Unit) {
        preRenderListeners.remove(listener)
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
        return VizMovingHead(movingHead, dmxUniverse, clock).also { it.addTo(VizScene(scene)) }
    }

    private fun startRender() {
        geom.computeBoundingSphere()
        this.obj = Points(geom, pointMaterial)
        scene.add(obj)
        val target = geom.boundingSphere!!.center.clone()
        controls?.target = target
        camera.lookAt(target)

        stopRendering = false
        render()
    }

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

        preRenderListeners.forEach { it.invoke() }

        val startTime = clock.now()
        renderer.render(scene, camera)
        facade.framerate.elapsed((clock.now() - startTime).asMillis().toInt())

        frameListeners.forEach { f -> f.onFrameReady(scene, camera) }
    }

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

    private val resizeDelay = 100

    fun resize() {
        container?.let {
            val canvas = renderer.domElement
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
            set(value) {
                this@Visualizer.container = value
            }

        var rotate: Boolean
            get() = this@Visualizer.rotate
            set(value) {
                this@Visualizer.rotate = value
            }

        var selectedSurface: VizSurface? = null

        val framerate = Framerate()

        fun resize() = this@Visualizer.resize()
        fun onMouseDown(event: MouseEvent) = this@Visualizer.onMouseDown(event)
    }

    companion object {
        private const val REFRESH_DELAY = 50 // ms
    }
}
