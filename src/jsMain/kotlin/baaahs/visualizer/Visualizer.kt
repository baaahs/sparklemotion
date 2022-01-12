package baaahs.visualizer

import baaahs.document
import baaahs.mapper.JsMapperUi
import baaahs.model.Model
import baaahs.model.ModelUnit
import baaahs.util.*
import baaahs.util.Clock
import baaahs.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import materialui.Icon
import materialui.icons.AspectRatio
import materialui.icons.PanTool
import materialui.icons.ThreeDRotation
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import three.js.*
import three_ext.OrbitControls
import three_ext.TransformControls
import three_ext.clear
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class Visualizer(
    private val clock: Clock,
    private val coroutineScope: CoroutineScope = GlobalScope
) : JsMapperUi.StatusListener {
    val facade = Facade()

    var stopRendering: Boolean = false
    var rotate: Boolean = false

    private var container: HTMLElement? = null
        set(value) {
            if (value != null) {
                field = value
                containerAttached()
            } else {
                containerWillDetach()
                field = value
            }
        }

    private var mapperIsRunning = false
        set(isRunning) {
            field = isRunning

            entityVisualizers.values.forEach { it.mapperIsRunning = isRunning }

            if (isRunning) {
                rotate = false
            }
        }

    private val prerenderListeners = mutableListOf<() -> Unit>()
    private val frameListeners = mutableListOf<FrameListener>()

    private val camera: PerspectiveCamera =
        PerspectiveCamera(45, 1.0, 1, 10000).apply {
            position.z = 1000.0
        }
    private val renderer = WebGLRenderer().apply {
        localClippingEnabled = true
    }
    private val canvas = renderer.domElement

    private val scene: Scene = Scene()

    private val transformControls = TransformControls(camera, canvas).also {
        it.space = "local"
        scene.add(it)
    }
    private var orbitControlsActive = false
    private val orbitControls = OrbitControls(camera, canvas).apply {
        minPolarAngle = PI / 2 - .25 // radians
        maxPolarAngle = PI / 2 + .25 // radians

        enableDamping = false
        enableKeys = false

        addEventListener("start") { orbitControlsActive = true }
        addEventListener("end") { orbitControlsActive = false }
    }

    private val raycaster = Raycaster()

    private val originDot: Mesh<*, *>

    private val entityVisualizers = mutableMapOf<Model.Entity, EntityVisualizer<*>>()
    private val selectionSpan = document.createElement("span") as HTMLSpanElement
    private var selectedEntity: EntityVisualizer<*>? = null
        set(value) {
            field?.let {
                console.log("Deselecting ${it.title}")
                it.selected = false
                transformControls.detach()
            }
            field = value

            if (value == null) {
                selectionSpan.style.display = "none"
                selectionSpan.innerText = ""
            } else {
                value.selected = true
                console.log("Selecting ${value.title}")
                selectionSpan.style.display = "inherit"
                selectionSpan.innerText = "Selected: ${value.title}"
                value.obj.let { transformControls.attach(it) }
            }

            facade.notifyChanged()
        }

    init {
        scene.add(camera)
        scene.add(GridHelper())
        scene.add(AxesHelper())
//        renderer.setPixelRatio(window.devicePixelRatio)

        raycaster.asDynamic().params.Points.threshold = 1
        originDot = Mesh(
            SphereBufferGeometry(1, 16, 16),
            MeshBasicMaterial().apply { color.set(0xff0000) }
        )
        scene.add(originDot)
    }

    init {
        canvas.addEventListener("pointerdown", this::onMouseDown)

        transformControls.addEventListener( "dragging-changed") {
            val isDragging = transformControls.dragging

            orbitControls.enabled = !isDragging

            if (!isDragging) {
                selectedEntity?.notifyChanged()
            }
        }
        transformControls.addEventListener( "change") {
            val entityVisualizer = transformControls.`object`?.entityVisualizer
            entityVisualizer?.notifyChanged()
            println("object = ${transformControls.`object`}")
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
        container!!.appendChild(canvas)
        container!!.appendChild(selectionSpan)

        resize()
        startRender()
    }

    private fun containerWillDetach() {
        container?.removeChild(canvas)
        container?.removeChild(selectionSpan)
        canvas.removeEventListener("pointerdown", this::onMouseDown)
        stopRendering = true
    }

    fun addPrerenderListener(callback: () -> Unit) {
        prerenderListeners.add(callback)
    }

    fun removePrerenderListener(callback: () -> Unit) {
        prerenderListeners.remove(callback)
    }

    fun addFrameListener(frameListener: FrameListener) {
        frameListeners.add(frameListener)
    }

    fun removeFrameListener(frameListener: FrameListener) {
        frameListeners.remove(frameListener)
    }

    private fun onMouseDown(event: Event) {
        if (orbitControlsActive || transformControls.dragging) return

        event as MouseEvent
        val bounds = (event.target as? HTMLCanvasElement)?.getBoundingClientRect()
        bounds?.let {
            val x = (event.clientX - bounds.x) / bounds.width
            val y = (event.clientY - bounds.y) / bounds.height
            val mouseClick = Vector2(x * 2 - 1, -y * 2 + 1)

            raycaster.setFromCamera(mouseClick, camera)
            val intersections = raycaster.intersectObjects(scene.children, true)
            var anyIntersection = false
            intersections.forEach { intersection ->
                val intersectedObject = intersection.`object`
                console.log("Found intersection with ${intersectedObject.name} at ${intersection.distance}.")
                intersectedObject.entityVisualizer?.let {
                    selectedEntity = it
                    anyIntersection = true
                    return@forEach
                }
            }

            if (!anyIntersection) {
                selectedEntity = null
            }
        }
    }

    fun add(entityVisualizer: EntityVisualizer<*>) {
        scene.add(entityVisualizer.obj)
    }

    private fun startRender() {
        stopRendering = false
        requestAnimationFrame()
    }

    private fun pointAtCenter() {
        val center = Vector3()
        Box3().expandByObject(scene).getCenter(center)
        pointAt(center)
    }

    private fun pointAt(location: Vector3) {
        orbitControls.target = location
        camera.lookAt(location)
    }

    fun render() {
        if (stopRendering) return

        prerenderListeners.forEach { value -> value.invoke() }

        if (!mapperIsRunning && rotate) {
            val rotSpeed = .01
            val x = camera.position.x
            val z = camera.position.z
            camera.position.x = x * cos(rotSpeed) + z * sin(rotSpeed)
            camera.position.z = z * cos(rotSpeed * 2) - x * sin(rotSpeed * 2)
            camera.lookAt(scene.position)
        }

        orbitControls.update()

        val startTime = clock.now()
        renderer.render(scene, camera)
        facade.framerate.elapsed((clock.now() - startTime).asMillis().toInt())

        frameListeners.forEach { f -> f.onFrameReady(scene, camera) }

        requestAnimationFrame()
    }

    private fun requestAnimationFrame() {
        window.requestAnimationFrame { render() }
    }

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

    private val resizeDelay = 100

    fun resize() {
        container?.let {
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

    fun release() {
        orbitControls.dispose()
        transformControls.dispose()
    }

    interface FrameListener {
        fun onFrameReady(scene: Scene, camera: Camera)
    }

    inner class Facade : baaahs.ui.Facade() {
        var container: HTMLElement?
            get() = this@Visualizer.container
            set(value) {
                this@Visualizer.container = value
            }

        var moveSnap: Double?
            get() = transformControls.translationSnap
            set(value) { transformControls.translationSnap = value }

        var rotateSnap: Double?
            get() = transformControls.rotationSnap
            set(value) { transformControls.rotationSnap = value }

        var scaleSnap: Double?
            get() = transformControls.scaleSnap
            set(value) { transformControls.scaleSnap = value }

        var transformMode: TransformMode
            get() = TransformMode.find(transformControls.mode)
            set(value) { transformControls.mode = value.modeName }

        var transformInLocalSpace: Boolean
            get() = transformControls.space == "local"
            set(value) { transformControls.space = if (value) "local" else "world" }

        var rotate: Boolean
            get() = this@Visualizer.rotate
            set(value) {
                this@Visualizer.rotate = value
            }

        var selectedEntity: EntityVisualizer<*>?
            get() = this@Visualizer.selectedEntity
            set(value) {
                this@Visualizer.selectedEntity = value
            }

        val framerate = Framerate()

        fun clear() = scene.clear()
        fun resize() = this@Visualizer.resize()

        fun select(entityVisualizer: EntityVisualizer<*>) {
            this@Visualizer.selectedEntity = entityVisualizer
        }
    }

    enum class TransformMode(val modeName: String, val icon: Icon) {
        Move("translate", PanTool) {
            override fun getGridUnitAdornment(modelUnit: ModelUnit): String = modelUnit.display

            override fun getGridSize(visualizer: Facade): Double? =
                visualizer.moveSnap

            override fun setGridSize(visualizer: Facade, value: Double?) {
                visualizer.moveSnap = value
            }
        },

        Rotate("rotate", ThreeDRotation) {
            override val defaultGridSize: Double
                get() = deg2rad(15.0)

            override fun getGridUnitAdornment(modelUnit: ModelUnit): String = "°"

            override fun getGridSize(visualizer: Facade): Double? =
                visualizer.rotateSnap

            override fun setGridSize(visualizer: Facade, value: Double?) {
                visualizer.rotateSnap = value
            }

            override fun toDisplayValue(size: Double): Double =
                (rad2deg(size) * 1000.0).roundToInt() / 1000.0

            override fun fromDisplayValue(size: Double?): Double? =
                size?.let { deg2rad(size) }
        },

        Scale("scale", AspectRatio) {
            override fun getGridUnitAdornment(modelUnit: ModelUnit): String = modelUnit.display

            override fun getGridSize(visualizer: Facade): Double? =
                visualizer.scaleSnap

            override fun setGridSize(visualizer: Facade, value: Double?) {
                visualizer.scaleSnap = value
            }
        };

        abstract fun getGridUnitAdornment(modelUnit: ModelUnit): String
        abstract fun getGridSize(visualizer: Visualizer.Facade): Double?
        abstract fun setGridSize(visualizer: Visualizer.Facade, value: Double?)
        open fun toDisplayValue(size: Double) = size
        open fun fromDisplayValue(size: Double?) = size
        open val defaultGridSize: Double = 1.0

        companion object {
            fun find(name: String) =
                values().find { it.modeName == name }
                    ?: error("huh?")
        }
    }

    companion object {
        private const val DEFAULT_REFRESH_DELAY = 50 // ms
    }
}
