package baaahs.ui

import baaahs.Logger
import external.react.memo
import org.w3c.dom.events.Event
import react.RMutableRef
import react.RProps
import kotlin.browser.window
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Get functional component from [func]
 */
fun <P : react.RProps> xComponent(
    name: String,
    isPure: Boolean = false,
    func: XBuilder.(props: P) -> Unit
): react.FunctionalComponent<P> {
    val logger = Logger(name)
    val component = { props: P ->
        react.buildElements {
            val xBuilder = XBuilder(logger)
            xBuilder.func(props)
            this.childList.addAll(xBuilder.childList)
            xBuilder.renderFinished()
        }
    }
    component.asDynamic().displayName = name
    return if (isPure)
        memo(component) { a, b ->
            val keys = hashSetOf(*a.keys).also {
                it.addAll(b.keys)
            }
            keys.all { key -> a[key] == b[key]}
        }
    else
        component
}

private val RProps.keys get() = jsObj.keys(this).unsafeCast<Array<String>>()
private operator fun RProps.get(key: String): Any? = asDynamic()[key]
private val jsObj = js("Object")

private class CounterIncr {
    private var i: Int = 0
    fun next(): Int = ++i
}

class XBuilder(val logger: Logger) : react.RBuilder() {
    private var firstTime = false
    private var dataIndex = 0
    private var sideEffectIndex = 0

    private val context = react.useMemo({
        firstTime = true
        Context()
    }, emptyArray())

    private val counterIncr = react.useState { CounterIncr() }
    private val counter = react.useState { counterIncr.first.next() }
    private var inRender = true
    private var stateHasChanged = false

    init {
        react.useEffectWithCleanup(emptyList()) {
            return@useEffectWithCleanup {
                context.sideEffects.forEach { it.runCleanups() }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> ref(block: (() -> T)? = null): RMutableRef<T> =
        react.useRef(if (block != null) block() else null as T)

    fun <T> memo(vararg watch: Any?, block: () -> T): T {
        return react.useMemo(block, watch)
    }

    fun <T> state(valueInitializer: () -> T): ReadWriteProperty<Any?, T> {
        @Suppress("UNREACHABLE_CODE")
        return if (firstTime) {
            val data = Data(logger, valueInitializer()) { forceRender() }
            context.data.add(data)
            return data
        } else {
            @Suppress("UNCHECKED_CAST")
            return context.data[dataIndex++] as Data<T>
        }
    }

    fun observe(item: Observable) {
        onChange("observe", item) {
            val observer = item.addObserver { forceRender() }
            withCleanup { observer.remove() }
        }
    }

    fun onChange(name: String, vararg watch: Any?, callback: SideEffect.() -> Unit) {
        return if (firstTime) {
            val sideEffect = SideEffect(watch, logger)
            context.sideEffects.add(sideEffect)
            sideEffect.callback()
        } else {
            val sideEffect = context.sideEffects[sideEffectIndex++]
            sideEffect.collectCleanups = false
            if (areSame(watch, sideEffect.lastWatchValues)) {
                logger.debug {
                    "Not running side effect $name " +
                            "(${watch.truncateStrings(12)} == ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
            } else {
                logger.info {
                    "Running side effect $name " +
                            "(${watch.truncateStrings(12)} != ${sideEffect.lastWatchValues.truncateStrings(12)}"
                }
                sideEffect.runCleanups()
                sideEffect.lastWatchValues = watch
                sideEffect.callback()
            }
        }
    }

    fun onMount(vararg watch: Any?, callback: SideEffect.() -> Unit) {
        react.useEffectWithCleanup(watch.toList()) {
            val sideEffect = SideEffect(watch, logger)
            sideEffect.callback()
            return@useEffectWithCleanup { sideEffect.runCleanups() }
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

    /**
     * Useful when calling a prop function which might make an impermissible state change
     * in a parent component from within a side effect.
     */
    fun later(block: () -> Unit) {
        window.setTimeout(block, 0)
    }

    fun forceRender() {
        if (inRender) {
            stateHasChanged = true
        } else {
            forceRenderNow(immediate = true)
        }
    }

    internal fun forceRenderNow(immediate: Boolean) {
        val triggerUpdate = { counter.second(counterIncr.first.next()) }
        if (immediate) {
            triggerUpdate()
        } else {
            later(triggerUpdate)
        }
    }

    internal fun renderFinished() {
        firstTime = false
        if (stateHasChanged) forceRenderNow(!inRender)
        inRender = false
    }

    private fun areSame(currentWatchValues: Array<out Any?>, priorWatchValues: Array<out Any?>): Boolean {
        return currentWatchValues.zip(priorWatchValues).all { (a, b) ->
            a == b
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

    private class Data<T>(private val logger: Logger, initialValue: T, private val onChange: () -> Unit): ReadWriteProperty<Any?, T> {
        var value: T = initialValue

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (this.value != value) {
//                logger.info { "${property.name} := ${value?.toString()?.truncate(12)}" }
                this.value = value
                onChange()
            }
        }
    }

    class SideEffect(
        internal var lastWatchValues: Array<out Any?>,
        private val logger: Logger
    ) {
        internal var collectCleanups: Boolean = true
        private var cleanups: MutableList<() -> Unit>? = null

        fun withCleanup(cleanup: () -> Unit) {
            if (collectCleanups) {
                if (cleanups == null) cleanups = mutableListOf()
                cleanups!!.add(cleanup)
            }
        }

        internal fun runCleanups() {
            cleanups?.forEach {
                try {
                    it.invoke()
                } catch (e: Exception) {
                    logger.error("Error in cleanup", e)
                }
            }
            cleanups?.clear()
            collectCleanups = true // re-collect cleanups
        }
    }
}

