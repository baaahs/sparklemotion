package baaahs.ui

import baaahs.Logger
import org.w3c.dom.events.Event
import react.*
import kotlin.browser.window
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface PluggableReact {
    fun buildElements(handler: RBuilder.() -> Unit): dynamic
    fun useEffectWithCleanup(dependencies: RDependenciesList? = null, effect: () -> RCleanup)
    fun <T> useMemo(callback: () -> T, dependencies: RDependenciesArray): T
    fun <T> useRef(initialValue: T): RMutableRef<T>
    fun <T> useState(valueInitializer: () -> T): Pair<T, RSetState<T>>
}

class RealReact : PluggableReact {
    override fun buildElements(handler: RBuilder.() -> Unit): dynamic {
        return react.buildElements(handler)
    }

    override fun useEffectWithCleanup(dependencies: RDependenciesList?, effect: () -> RCleanup) {
        return react.useEffectWithCleanup(dependencies, effect)
    }

    override fun <T> useMemo(callback: () -> T, dependencies: RDependenciesArray): T {
        return react.useMemo(callback, dependencies)
    }

    override fun <T> useRef(initialValue: T): RMutableRef<T> {
        return useRef(initialValue)
    }

    override fun <T> useState(valueInitializer: () -> T): Pair<T, RSetState<T>> {
        return react.useState(valueInitializer)
    }
}

var _react: PluggableReact = RealReact()

/**
 * Get functional component from [func]
 */
fun <P : react.RProps> xComponent(
    name: String,
    func: XBuilder.(props: P) -> Unit
): react.FunctionalComponent<P> {
    val logger = Logger(name)
    val component = { props: P ->
        _react.buildElements {
            val xBuilder = XBuilder(logger)
            xBuilder.func(props)
            this.childList.addAll(xBuilder.childList)
            xBuilder.renderFinished()
        }
    }
    component.asDynamic().displayName = name
    return component
}

class XBuilder(val logger: Logger) : react.RBuilder() {
    private var firstTime = false
    private var dataIndex = 0
    private var sideEffectIndex = 0

    private val context = _react.useMemo({
        firstTime = true
        Context()
    }, emptyArray())

    private val counter = _react.useState { 0 }
    private var internalCounter = 0

    init {
        _react.useEffectWithCleanup(emptyList()) {
            return@useEffectWithCleanup {
                context.sideEffects.forEach { it.runCleanups() }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> ref(): RMutableRef<T> = _react.useRef(null as T)

    fun <T> memo(vararg watch: Any?, block: () -> T): T {
        return _react.useMemo(block, watch)
    }

    fun <T> state(valueInitializer: () -> T): ReadWriteProperty<Any?, T> {
        @Suppress("UNREACHABLE_CODE")
        return if (firstTime) {
            val data = Data(valueInitializer()) {
                counter.second(++internalCounter)
            }
            context.data.add(data)
            return data
        } else {
            @Suppress("UNCHECKED_CAST")
            return context.data[dataIndex++] as Data<T>
        }
    }

    fun observe(item: Observable) {
        sideEffect("observe", item) {
            val observer = object : Observer {
                override fun notifyChanged() = forceRender()
            }
            item.addObserver(observer)
            withCleanup {
                item.removeObserver(observer)
            }
        }
    }

    fun sideEffect(name: String, vararg watch: Any?, callback: SideEffect.() -> Unit) {
        return if (firstTime) {
            val sideEffect = SideEffect(watch)
            context.sideEffects.add(sideEffect)
            sideEffect.callback()
        } else {
            val sideEffect = context.sideEffects[sideEffectIndex++]
            sideEffect.firstTime = false
            if (areSame(watch, sideEffect.lastWatchValues)) {
                logger.debug {
                    "Not running side effect $name " +
                            "(${watch.truncateStrings(12)} == ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
            } else {
                logger.debug {
                    "Running side effect $name " +
                            "(${watch.truncateStrings(12)} != ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
                sideEffect.runCleanups()
                sideEffect.lastWatchValues = watch
                sideEffect.callback()
            }
        }
    }

    fun <T : Function<*>> handler(name: String, vararg watch: Any?, block: T): T {
        val handler = context.handlers.getOrPut(name) { Handler(watch, block) }
        return if (areSame(watch, handler.lastWatchValues)) {
            handler.block.unsafeCast<T>()
        } else {
            handler.block = block
            block
        }
    }

    fun eventHandler(name: String, vararg watch: Any?, block: (Event) -> Unit): (Event) -> Unit {
        return handler(name, watch = *watch, block = block)
    }

    fun eventHandler(block: Function<*>): (Event) -> Unit {
        @Suppress("UNCHECKED_CAST")
        return handler("event handler", block, block = block as (Event) -> Unit)
    }

    fun whenMounted(block: SideEffect.() -> Unit) {
        _react.useEffectWithCleanup(emptyList()) {
            val sideEffect = SideEffect(emptyArray())
            sideEffect.block()
            return@useEffectWithCleanup { sideEffect.runCleanups() }
        }
    }

    /**
     * Useful when calling a prop function which might make an impermissible state change
     * in a parent component from within a side effect.
     */
    fun later(block: () -> Unit) {
        window.setTimeout(block, 0)
    }

    fun forceRender() {
        counter.second(++internalCounter)
    }

    internal fun renderFinished() {
        firstTime = false
    }

    private fun areSame(currentWatchValues: Array<out Any?>, priorWatchValues: Array<out Any?>): Boolean {
        return currentWatchValues.zip(priorWatchValues).all { (a, b) ->
            if (a is String && b is String) {
                a == b
            } else if (a is Number && b is Number) {
                a == b
            } else {
                a === b
            }
        }
    }

    private class Context {
        val data: MutableList<Data<*>> = mutableListOf()
        val sideEffects: MutableList<SideEffect> = mutableListOf()
        val handlers: MutableMap<String, Handler> = mutableMapOf()
    }

    private class Handler(
        internal var lastWatchValues: Array<out Any?>,
        var block: Function<*>
    )

    private class Data<T>(initialValue: T, private val onChange: () -> Unit) : ReadWriteProperty<Any?, T> {
        var value: T = initialValue

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            logger.debug { "${property.name} := ${value?.toString()?.truncate(12)}" }
            if (this.value != value) {
                this.value = value
                onChange()
            }
        }
    }

    class SideEffect(
        internal var lastWatchValues: Array<out Any?>
    ) {
        internal var firstTime: Boolean = true
        internal var cleanups: MutableList<() -> Unit>? = null

        fun withCleanup(cleanup: () -> Unit) {
            if (firstTime) {
                if (cleanups == null) cleanups = mutableListOf()
                cleanups!!.add(cleanup)
            }
        }

        internal fun runCleanups() {
            cleanups?.forEach { it.invoke() }
            cleanups?.clear()
            firstTime = true // re-collect cleanups
        }
    }

    companion object {
        private val logger = Logger("XBuilder")
    }
}

