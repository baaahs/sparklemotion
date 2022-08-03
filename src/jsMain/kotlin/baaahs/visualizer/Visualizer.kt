package baaahs.visualizer

import baaahs.document
import baaahs.getBang
import baaahs.mapper.JsMapper
import baaahs.util.Clock
import baaahs.util.Framerate
import baaahs.util.Logger
import baaahs.util.asMillis
import baaahs.window
import kotlinx.css.hyphenize
import kotlinx.js.jso
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import three.js.*
import three_ext.OrbitControls
import three_ext.clear
import three_ext.set
import kotlin.math.*
import kotlin.reflect.KClass

class Visualizer(
    clock: Clock
) : IVisualizer, BaseVisualizer(clock) {
    override val facade = Facade()

    private val selectionSpan = document.createElement("span") as HTMLSpanElement

    var selectedEntity: ItemVisualizer<*>? = null

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

    private val itemVisualizers = arrayListOf<ItemVisualizer<*>>()

    init {
        addPrerenderListener {
            itemVisualizers.forEach { it.applyStyles() }
        }
    }

    private fun containerAttached() {
        container!!.appendChild(canvas)
        container!!.appendChild(selectionSpan)

        resize()
        startRendering()
    }

    private fun containerWillDetach() {
        container?.removeChild(canvas)
        container?.removeChild(selectionSpan)
        stopRendering()
    }

    override fun clear() {
        itemVisualizers.clear()
        scene.clear()
        sceneNeedsUpdate = true
    }

    override fun add(itemVisualizer: ItemVisualizer<*>) {
        itemVisualizers.add(itemVisualizer)
        scene.add(itemVisualizer.obj)
        sceneNeedsUpdate = true
    }

    override fun onSelectionChange(obj: Object3D?, priorObj: Object3D?) {
        val vizObj = findParentEntityVisualizer(obj)

        if (vizObj == null) {
            selectionSpan.style.display = "none"
            selectionSpan.innerText = ""
        } else {
            val entityVisualizer = vizObj.itemVisualizer!!
            entityVisualizer.selected = true
            console.log("Selecting ${entityVisualizer.title}")
            selectionSpan.style.display = "inherit"
            selectionSpan.innerText = "Selected: ${entityVisualizer.title}"
//            entityVisualizer.obj.let { transformControls.attach(it) }
        }
        super.onSelectionChange(obj, priorObj)
    }

    private fun findParentEntityVisualizer(obj: Object3D?): Object3D? {
        var curObj = obj
        while (curObj != null && curObj.itemVisualizer == null) {
            curObj = curObj.parent
        }
        return curObj
    }

    public override fun stopRendering() {
        super.stopRendering()
    }

    inner class Facade : BaseVisualizer.Facade() {
        val selectedEntity get() = this@Visualizer.selectedEntity

        var container: HTMLElement?
            get() = this@Visualizer.container
            set(value) {
                this@Visualizer.container = value
            }

        fun clear() = this@Visualizer.clear()

        fun select(itemVisualizer: ItemVisualizer<*>) {
            this@Visualizer.selectedEntity = itemVisualizer
        }
    }
}

open class BaseVisualizer(
    private val clock: Clock
) : JsMapper.StatusListener {
    open val facade = Facade()

    private var stopRendering: Boolean = false
    var rotate: Boolean = false

    private var mapperIsRunning = false
        set(isRunning) {
            field = isRunning

            // TODO: still should do this somehow...
//            entityVisualizers.values.forEach { it.mapperIsRunning = isRunning }

            if (isRunning) {
                rotate = false
            }
        }

    private val prerenderListeners = mutableListOf<() -> Unit>()
    private val frameListeners = mutableListOf<FrameListener>()

    protected val camera: PerspectiveCamera =
        PerspectiveCamera(45, 1.0, 0.1, 10000).apply {
            position.z = 1000.0
        }
    private val ambientLight = AmbientLight(Color(0xFFFFFF), .25)
    private val directionalLight = DirectionalLight(Color(0xFFFFFF), 1)
    private val renderer = WebGLRenderer().apply {
        localClippingEnabled = true
    }
    protected val canvas = renderer.domElement

    protected val realScene = Scene().apply { autoUpdate = false }
    protected val scene = Group().also { realScene.add(it) }
    protected var sceneNeedsUpdate = true

    /**
     * The order that these event listeners are registered matters; can't think of a
     * better way to allow subclasses to inject listeners before ours.
     */
    protected open val extensions = listOf(
        extension { SelectExtension() },
        extension { OrbitControlsExtension() }
    )
    @Suppress("LeakingThis")
    private val activeExtensions = extensions.associate { (key, factory) ->
        key to factory()
    }

    init {
        @Suppress("LeakingThis")
        activeExtensions.values.forEach { it.attach() }
    }

    inner class SelectExtension : Extension(SelectExtension::class) {
        override fun attach() {
            canvas.addEventListener("pointerdown", this@BaseVisualizer::onMouseDown)
        }

        override fun release() {
            canvas.removeEventListener("pointerdown", this@BaseVisualizer::onMouseDown)
        }
    }

    private var orbitControlsActive = false
    inner class OrbitControlsExtension : Extension(OrbitControlsExtension::class) {
        val orbitControls by lazy {
            OrbitControls(camera, canvas).apply {
                minPolarAngle = PI / 2 - .25 // radians
                maxPolarAngle = PI / 2 + .25 // radians

                enableDamping = false
                enableKeys = false

                addEventListener("start") { orbitControlsActive = true }
                addEventListener("end") { orbitControlsActive = false }
            }
        }

        override fun attach() {
            orbitControls
        }

        fun update() = orbitControls.update()
    }
    private val orbitControlsExtension = findExtension(OrbitControlsExtension::class)

    private val raycaster = Raycaster()

    private val originDot: Mesh<*, *>

    protected var selectedObject: Object3D? = null
        set(value) {
            if (field == value) return

            val oldValue = field
            oldValue?.dispatchEvent(EventType.Deselect)
            value?.dispatchEvent(EventType.Select)

            field = value
            onSelectionChange(value, oldValue)
        }

    open fun onSelectionChange(obj: Object3D?, priorObj: Object3D?) {
        facade.notifyChanged()
    }

    init {
        realScene.add(camera)
        realScene.add(ambientLight)
        realScene.add(directionalLight)
//        renderer.setPixelRatio(window.devicePixelRatio)

        raycaster.asDynamic().params.Points.threshold = 1
        originDot = Mesh(
            SphereBufferGeometry(1, 16, 16),
            MeshBasicMaterial().apply {
                color.set(0xff0000)
                opacity = .25
            }
        ).apply { name = "Origin dot" }
        realScene.add(originDot)

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

    fun fitCameraToObject(offset: Double = 1.25) {
        val boundingBox = Box3()
        if (scene.children.isEmpty()) {
            logger.warn { "No objects in scene, bailing." }
            return
        }
        boundingBox.setFromObject(scene)

        val center = boundingBox.getCenter(Vector3())
        val size = boundingBox.getSize(Vector3())

        // get the max side of the bounding box (fits to width OR height as needed )
        val fov = camera.fov.toDouble() * (PI / 180)
        val fovh = 2*atan(tan(fov/2) * camera.aspect.toDouble())
        val dx = size.z / 2 + abs( size.x / 2 / tan( fovh / 2 ) )
        val dy = size.z / 2 + abs( size.y / 2 / tan( fov / 2 ) )
        var cameraZ = max(dx, dy)

        cameraZ *= offset // zoom out a little so that objects don't fill the screen

        camera.position.x = center.x
        camera.position.y = center.y
        camera.position.z = cameraZ

        val minZ = boundingBox.min.z
        val cameraToFarEdge = if (minZ < 0) -minZ + cameraZ else cameraZ - minZ

        camera.far = cameraToFarEdge * 3
        camera.updateProjectionMatrix()

        val controls = orbitControlsExtension.orbitControls
        // set camera to rotate around center of loaded object
        controls.target = center

        // prevent camera from zooming out far enough to create far plane cutoff
        controls.maxDistance = cameraToFarEdge * 2

        controls.saveState()
    }

    fun <T : Extension> findExtension(tClass: KClass<T>): T {
        return activeExtensions.getBang(tClass, "visualizer extension") as T
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
        if (inUserInteraction()) return

        event as MouseEvent
        val bounds = (event.target as? HTMLCanvasElement)?.getBoundingClientRect()
        bounds?.let {
            val x = (event.clientX - bounds.x) / bounds.width
            val y = (event.clientY - bounds.y) / bounds.height
            val mouseClick = Vector2(x * 2 - 1, -y * 2 + 1)

            raycaster.setFromCamera(mouseClick, camera)
            val intersections = raycaster.intersectObject(scene, true)
            onObjectClick(intersections.toList())
        }
    }

    protected open fun onObjectClick(intersections: List<Intersection>) {
        intersections.firstOrNull()?.let { intersection ->
            val obj = intersection.`object`
            console.log("Found intersection with ${obj.name} at ${intersection.distance}.")
            onObjectClick(obj)
            return
        }
        onObjectClick(null)
    }

    protected open fun onObjectClick(obj: Object3D?) {
        selectedObject = obj
        obj?.dispatchEvent(EventType.Click)
    }

    open fun inUserInteraction() = orbitControlsActive

    protected fun startRendering() {
        stopRendering = false
        requestAnimationFrame()
    }

    protected open fun stopRendering() {
        stopRendering = true
    }

    private fun pointAtCenter() {
        val center = Vector3()
        Box3().expandByObject(scene).getCenter(center)
        pointAt(center)
    }

    private fun pointAt(location: Vector3) {
        orbitControlsExtension.orbitControls.target = location
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

        orbitControlsExtension.update()

        directionalLight.position.set(camera.position)
        directionalLight.position.y *= 0.125

        val startTime = clock.now()
        if (sceneNeedsUpdate) {
            realScene.updateMatrixWorld()
            fitCameraToObject()
            sceneNeedsUpdate = false
        }
        renderer.render(realScene, camera)
        facade.framerate.elapsed((clock.now() - startTime).asMillis().toInt())

        frameListeners.forEach { f -> f.onFrameReady(realScene, camera) }

        requestAnimationFrame()
    }

    private fun requestAnimationFrame() {
        window.requestAnimationFrame { render() }
    }

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

    private val resizeDelay = 100

    fun resize() {
        (canvas.parentElement as? HTMLElement)?.let { parent ->
            canvas.width = parent.offsetWidth
            canvas.height = parent.offsetHeight

            camera.aspect = parent.offsetWidth.toDouble() / parent.offsetHeight
            camera.updateProjectionMatrix()

            renderer.setSize(parent.offsetWidth, parent.offsetHeight, updateStyle = false)
        }
    }

    override fun mapperStatusChanged(isRunning: Boolean) {
        mapperIsRunning = isRunning
    }

    open fun release() {
        activeExtensions.values.reversed().forEach { it.release() }
    }

    interface FrameListener {
        fun onFrameReady(scene: Scene, camera: Camera)
    }

    open inner class Facade : baaahs.ui.Facade() {
        val canvas get() = this@BaseVisualizer.canvas
        val selectedObject get() = this@BaseVisualizer.selectedObject

        var rotate: Boolean
            get() = this@BaseVisualizer.rotate
            set(value) {
                this@BaseVisualizer.rotate = value
            }

        val framerate = Framerate()

        fun resize() = this@BaseVisualizer.resize()
    }

    fun Object3D.dispatchEvent(eventType: EventType) {
        dispatchEvent(jso { type = eventType.name.hyphenize().lowercase() })
    }

    enum class EventType {
        Select,
        Deselect,
        Click,
        Transform
    }

    companion object {
        private const val DEFAULT_REFRESH_DELAY = 50 // ms
        private val logger = Logger<BaseVisualizer>()
    }
}

inline fun <reified T: Extension> extension(
    noinline factory: () -> T
): Pair<KClass<T>, () -> T> {
    return T::class to factory
}

abstract class Extension(val key: KClass<out Extension>) {
    open fun attach() {}
    open fun release() {}
}