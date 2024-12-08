package baaahs.visualizer

import baaahs.visualizer.entity.ItemVisualizer
import three.Camera
import three.Scene
import web.html.HTMLCanvasElement
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class Extension(val key: KClass<out Extension>) {
    private val initializers = mutableListOf<Initializable<*>>()
    private val attachments = mutableListOf<Initializable<*>>()

    /** Use this to define properties that should be initialized when the visualizer is created. */
    protected fun <T> initializer(valueInitializer: VisualizerContext.() -> T): ReadOnlyProperty<Any?, T> =
        Initializable(valueInitializer).also {
            initializers.add(it)
        }

    /** Use this to define properties that should be initialized when the visualizer is attached. */
    protected fun <T> attachment(valueInitializer: VisualizerContext.() -> T): ReadOnlyProperty<Any?, T> =
        Initializable(valueInitializer).also {
            attachments.add(it)
        }

    fun initialize(context: VisualizerContext) {
        initializers.forEach { it.run(context) }
    }

    fun prepareForAttach(context: VisualizerContext) {
        attachments.forEach { it.run(context) }
    }

    open fun VisualizerContext.attach() {}
    open fun VisualizerContext.resize(width: Int, height: Int) {}
    open fun VisualizerContext.isInUserInteraction(): Boolean = false
    open fun VisualizerContext.beforeRender() {}
    open fun VisualizerContext.render() {}
    open fun onSelectionChange(selection: ItemVisualizer<*>?, priorSelection: ItemVisualizer<*>?) {}
    open fun VisualizerContext.clearScene()  {}
    open fun VisualizerContext.detach() {}
    open fun VisualizerContext.release() {}

    class VisualizerContext(
        val camera: Camera,
        val canvas: HTMLCanvasElement,
        val scene: Scene
    )

    private class Initializable<T>(
        val initializer: VisualizerContext.() -> T
    ) : ReadOnlyProperty<Any?, T> {
        private var value: T? = null

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value ?: error("Property not initialized.")
        }

        fun run(context: VisualizerContext) {
            value = context.initializer()
        }
    }
}