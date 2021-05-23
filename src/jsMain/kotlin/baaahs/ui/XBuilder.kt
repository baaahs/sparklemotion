package baaahs.ui

import baaahs.util.Logger
import baaahs.window
import external.react.memo
import org.w3c.dom.events.Event
import react.RMutableRef
import react.RProps
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Get functional component from [func]
 */
fun <P : RProps> xComponent(
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
    private var changeDetectorIndex = 0

    private val context = react.useMemo({
        firstTime = true
        Context()
    }, emptyArray())

    private val counterIncr = react.rawUseState { CounterIncr() }
    private val counter = react.rawUseState { counterIncr.component1().next() }
    private var inRender = true
    private var stateHasChanged = false

    init {
        react.useEffectWithCleanup(emptyList()) {
            return@useEffectWithCleanup {
                context.changeDetectors.forEach { it.runCleanups() }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> ref(block: (() -> T)? = null): RMutableRef<T> =
        react.useRef(if (block != null) block() else null as T)

    fun <T> memo(vararg watch: Any?, callback: ChangeDetector.() -> T): T {
        return if (firstTime) {
            val memo = Memo<T>(watch, logger, callback)
            context.changeDetectors.add(memo)
            memo.memoizedValue
        } else {
            @Suppress("UNCHECKED_CAST")
            val memo = context.changeDetectors[changeDetectorIndex++] as Memo<T>
            if (memo.hasChanged(watch)) {
                logger.info { "Re-memoizing (${memo.describeDifference(watch)})" }
                memo.runCleanups()
                memo.rememoize(callback)
                memo.lastWatchValues = watch
            }
            memo.memoizedValue
        }
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

    fun observe(item: IObservable) {
        onChange("observe", item) {
            val observer = item.addObserver { forceRender() }
            withCleanup { observer.remove() }
        }
    }

    fun onChange(name: String, vararg watch: Any?, callback: ChangeDetector.() -> Unit) {
        return if (firstTime) {
            val changeDetector = ChangeDetector(watch, logger)
            context.changeDetectors.add(changeDetector)
            changeDetector.callback()
        } else {
            val changeDetector = context.changeDetectors[changeDetectorIndex++]
            if (changeDetector.hasChanged(watch)) {
                logger.debug { "Running onChange($name: ${changeDetector.describeDifference(watch)})" }
                changeDetector.runCleanups()
                changeDetector.lastWatchValues = watch
                changeDetector.callback()
            } else {
                logger.debug { "Not running onChange($name)" }
            }
        }
    }

    fun onMount(vararg watch: Any?, callback: ChangeDetector.() -> Unit) {
        react.useEffectWithCleanup(watch.toList()) {
            val sideEffect = ChangeDetector(watch, logger)
            sideEffect.callback()
            return@useEffectWithCleanup { sideEffect.runCleanups() }
        }
    }

    fun <T : Function<*>> callback(vararg dependencies: dynamic, callback: T): T {
        return react.useCallback(callback, dependencies)
    }

    fun <T : Function<*>> handler(name: String, vararg watch: Any?, block: T): T {
    @Suppress("FunctionName")
    private fun <T : Function<*>> _handler(name: String, watch: Array<out Any?>, block: T): T {
        val handler = context.handlers.getOrPut(name) { Handler(watch, logger, block) }
        return if (handler.hasChanged(watch)) {
            handler.block = block

            // Unfortunate that we can't make this a ChangeHandler.Function<*> to allow cleanups.
            block
        } else {
            handler.block.unsafeCast<T>()
        }
    }

    fun <T : Function<*>> handler(vararg watch: Any?, block: T): ReadOnlyProperty<Any?, T> {
        return ReadOnlyProperty { _, property ->
            _handler(property.name, watch, block)
        }
    }

    fun eventHandler(vararg watch: Any?, block: (Event) -> Unit): ReadOnlyProperty<Any?, (Event) -> Unit> {
        return ReadOnlyProperty { _, property ->
            _handler(property.name, watch) { event: Event -> block(event) }
        }
    }

    fun <T> eventHandler(valueInitializer: () -> T): ReadWriteProperty<Any?, T> {
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

    private fun forceRenderNow(immediate: Boolean) {
        val triggerUpdate = { counter.component2()(counterIncr.component1().next()) }
        if (immediate) {
            triggerUpdate()
        } else {
            later { triggerUpdate() }
        }
    }

    internal fun renderFinished() {
        firstTime = false
        if (stateHasChanged) forceRenderNow(!inRender)
        inRender = false
    }

    private class Context {
        val data: MutableList<Data<*>> = mutableListOf()
        val changeDetectors: MutableList<ChangeDetector> = mutableListOf()
        val handlers: MutableMap<String, Handler> = mutableMapOf()
    }

    private class Handler(
        lastWatchValues: Array<out Any?>,
        logger: Logger,
        var block: Function<*>
    ) : ChangeDetector(lastWatchValues, logger)

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

    private class Memo<T>(
        lastWatchValues: Array<out Any?>,
        logger: Logger,
        callback: ChangeDetector.() -> T
    ) : ChangeDetector(lastWatchValues, logger) {
        var memoizedValue = callback()

        fun rememoize(callback: ChangeDetector.() -> T) {
            memoizedValue = callback()
        }
    }

    open class ChangeDetector(
        internal var lastWatchValues: Array<out Any?>,
        private val logger: Logger
    ) {
        private var cleanups: MutableList<() -> Unit>? = null

        fun hasChanged(currentWatchValues: Array<out Any?>): Boolean {
            return currentWatchValues.zip(lastWatchValues).any { (a, b) -> a != b }
        }

        fun withCleanup(cleanup: () -> Unit) {
            if (cleanups == null) cleanups = mutableListOf()
            cleanups!!.add(cleanup)
        }

        internal fun runCleanups() {
            cleanups?.forEach {
                try {
                    it.invoke()
                } catch (e: Exception) {
                    logger.error(e) { "Error in cleanup" }
                }
            }
            cleanups?.clear()
        }

        fun describeDifference(newValues: Array<out Any?>): String {
            return "${newValues.truncateStrings(12)} != ${lastWatchValues.truncateStrings(12)})"
        }
    }
}

