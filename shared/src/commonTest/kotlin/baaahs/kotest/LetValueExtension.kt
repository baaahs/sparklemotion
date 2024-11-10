package baaahs.kotest

import baaahs.kotest.LetValueExtension.logger
import baaahs.util.Logger
import io.kotest.common.TestPath
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.TestCase
import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object LetValueExtension {
    val logger = Logger<LetValueExtension>()
}

class NestLevel {
    var level = 0
    val finalizers = mutableListOf<() -> Unit>()
}

object RunContext {
    private val rootScopeNodes = mutableMapOf<RootScope, RootScopeNode>()
    private val containerScopeNodes = mutableMapOf<ContainerScope, ContainerScopeNode>()

    val rootScopeValues = mutableMapOf<RootScope, ArrayList<*>>()
    var currentRootScope: RootScopeNode? = null

    val nodeToValues = mutableMapOf<Node, ArrayList<*>>()

    fun getRootScopeNode(rootScope: RootScope): RootScopeNode =
        rootScopeNodes.getOrPut(rootScope) { RootScopeNode(rootScope) }

    fun getContainerScopeNode(containerScope: ContainerScope): ContainerScopeNode =
        containerScopeNodes.getOrPut(containerScope) { ContainerScopeNode(containerScope) }
}

fun <T> RootScope.value(factory: () -> T): LetValue.PropertyCreator<T> {
    return LetValueCreator(factory, RunContext.getRootScopeNode(this), { finalizers.add(it) })
}

fun <T> RootScope.value(letValue: T, factory: () -> T) {
    LetValueGetter.override(RunContext.getRootScopeNode(this), factory)
}

fun <T> ContainerScope.value(factory: () -> T): LetValue.PropertyCreator<T> {
    return LetValueCreator(factory, RunContext.getContainerScopeNode(this), { finalizers.add(it) })
}

fun <T> ContainerScope.value(letValue: T, factory: () -> T) {
    logger.debug { "ContainerScope.value override" }
    LetValueGetter.override(RunContext.getContainerScopeNode(this), factory)
}


interface LetValue<out T> : ReadOnlyProperty<Any?, LetValue<T>> {
    operator fun invoke(): T

    interface PropertyCreator<out T> {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T>
    }
}

object Thing : BeforeContainerListener {
    override suspend fun beforeContainer(testCase: TestCase) {
        super.beforeContainer(testCase)
    }
}

interface Node {
    val testCase: TestCase?
    val testPath: TestPath?

    fun listen(listener: LetValueGetter<*>)
}

class RootScopeNode(val rootScope: RootScope) : Node {
    override val testCase: TestCase?
        get() = null
    override val testPath: TestPath?
        get() = null

    override fun listen(listener: LetValueGetter<*>) {
        // No-op.
    }
}

class ContainerScopeNode(val containerScope: ContainerScope) : Node {
    override val testCase: TestCase?
        get() = containerScope.testCase
    override val testPath: TestPath?
        get() = containerScope.testCase.descriptor.path()
    private val listeners = mutableListOf<LetValueGetter<*>>()

    init {
        containerScope.beforeContainer { testCase ->
            logger.debug { "ContainerScopeNode.beforeContainer: ${testCase.descriptor.path()}" }
            listeners.forEach { it.beforeExecuteGroup(testCase) }
        }

        containerScope.beforeEach { testCase ->
            LetValuesState.currentTestCase = testCase
            logger.debug { "ContainerScopeNode.beforeTest: ${testCase.descriptor.path()} ${testCase.type}" }
            listeners.forEach { it.beforeInvocation(testCase) }
        }

        containerScope.afterEach { (testCase, testResult) ->
            logger.debug { "ContainerScopeNode.afterTest: ${testCase.descriptor.path()}" }
            listeners.forEach { it.afterInvocation(testCase) }
            LetValuesState.currentTestCase = null
        }

        containerScope.afterContainer { (testCase, testResult) ->
            logger.debug { "ContainerScopeNode.afterContainer: ${testCase.descriptor.path()}" }
            listeners.forEach { it.afterExecuteGroup(testCase) }
        }
    }

    override fun listen(listener: LetValueGetter<*>) {
        listeners.add(listener)
    }
}

class LetValueCreator<T>(
    val factory: () -> T, val node: Node, val afterGroupDeclaration: NestLevel.(() -> Unit) -> Unit
) : LetValue.PropertyCreator<T> {

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        LetValueGetter(factory, node, property.name)
}

class LetValueGetter<T>(baseFactory: () -> T, node: Node, val name: String) : ReadOnlyProperty<Any?, T> {
    private val paths = hashMapOf(node.testCase to baseFactory)

    private var initializedForTest = false
    private var valueForTest: T? = null

    init {
        node.listen(this)
        logger.debug { "$name is declared at ${node.testPath}" }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        logger.debug { "LetValueGetter.getValue: $name inTest = ${LetValuesState.inTest} current = ${LetValuesState.current}" }
        if (!LetValuesState.inTest) {
            if (LetValuesState.current != null) {
                throw IllegalStateException("$name can't be used from beforeEach or afterEach")
            }
            LetValuesState.current = this
            @Suppress("UNCHECKED_CAST")
            return null as T
        }

        if (initializedForTest) {
            @Suppress("UNCHECKED_CAST")
            return valueForTest as T
        }

        var searchPath = LetValuesState.currentTestCase
        while (searchPath != null && !paths.containsKey(searchPath)) {
            searchPath = searchPath.parent
        }
        logger.debug { "For $name, we use value from ${searchPath?.descriptor?.path()}." }
        val fn = paths[searchPath]
            ?: throw IllegalStateException("no let value for ${LetValuesState.currentTestCase}")
        return fn().also {
            valueForTest = it
            initializedForTest = true
        }
    }

    fun override(node: Node, factory: () -> T) {
        logger.debug { "LetValueGetter.override: $name for context = ${node.testPath}" }
        if (paths.containsKey(node.testCase)) {
            throw IllegalStateException("value already given for $name in $node")
        }
        logger.debug { "$name is overridden at ${node.testPath}" }
        paths[node.testCase] = factory
    }

    fun reset() {
        initializedForTest = false
        valueForTest = null
    }

    fun beforeExecuteGroup(group: TestCase) {
    }

    fun beforeInvocation(testCase: TestCase) {
//        LetValuesState.inTest = true
        logger.debug { "LetValueGetter.beforeInvocation: $name, inTest := ${LetValuesState.inTest}" }
    }

    fun afterInvocation(testCase: TestCase) {
        reset()
        logger.debug { "LetValueGetter.afterInvocation: $name, inTest := ${LetValuesState.inTest}" }
    }

    fun afterExecuteGroup(testCase: TestCase) {
        if (LetValuesState.current != null) {
            throw IllegalStateException("$name can't be used from beforeEachGroup or afterEachGroup")
        }
    }

    companion object {
        internal fun <T> override(node: Node, factory: () -> T) {
            LetValuesState.override(node, factory)
        }
    }
}

@ThreadLocal
private object LetValuesState {
    internal var current: LetValueGetter<*>? = null

    var currentTestCase: TestCase? = null
    val inTest: Boolean get() = currentTestCase != null

    fun <T> override(node: Node, factory: () -> T) {
        val contextLetValue = current
        current = null

        if (contextLetValue == null) {
            throw IllegalStateException("no context for value override")
        } else {
            @Suppress("UNCHECKED_CAST")
            contextLetValue.override(node, factory as () -> Nothing)
        }
    }
}