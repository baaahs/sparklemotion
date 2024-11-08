package baaahs.kotest

import baaahs.util.Logger
import io.kotest.common.TestPath
import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.native.concurrent.ThreadLocal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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
    LetValueExtension.Companion.logger.info { "ContainerScope.value override" }
    LetValueGetter.override(RunContext.getContainerScopeNode(this), factory)
}


class LetValueExtension : BeforeContainerListener, BeforeEachListener, AfterEachListener, AfterContainerListener {
    override suspend fun beforeContainer(testCase: TestCase) {
        logger.info { "LetValueExtension.beforeContainer: ${testCase.descriptor.path()}" }
    }

    override suspend fun beforeEach(testCase: TestCase) {
        logger.info { "LetValueExtension.beforeEach: ${testCase.descriptor.path()}" }
        LetValuesState.currentTestCase = testCase
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        LetValuesState.currentTestCase = null
        logger.info { "LetValueExtension.afterEach: ${testCase.descriptor.path()}" }
    }

    override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
        logger.info { "LetValueExtension.afterContainer: ${testCase.descriptor.path()}" }
    }

    companion object {
        val logger = Logger<LetValueExtension>()
    }
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

    fun beforeGroup(block: TestCase.() -> Unit)
    fun afterGroup(block: TestCase.() -> Unit)

    fun beforeTest(block: TestCase.() -> Unit)
    fun afterTest(block: TestCase.(TestResult) -> Unit)
}

class RootScopeNode(val rootScope: RootScope) : Node {
    override val testCase: TestCase?
        get() = null
    override val testPath: TestPath?
        get() = null

    override fun beforeGroup(block: TestCase.() -> Unit) {
        LetValueExtension.Companion.logger.info { "RootScopeNode.beforeGroup ${rootScope}" }
    }

    override fun afterGroup(block: TestCase.() -> Unit) {
        LetValueExtension.Companion.logger.info { "RootScopeNode.afterGroup ${rootScope}" }
    }

    override fun beforeTest(block: TestCase.() -> Unit) {
        LetValueExtension.Companion.logger.info { "RootScopeNode.beforeTest ${rootScope}" }
    }

    override fun afterTest(block: TestCase.(TestResult) -> Unit) {
        LetValueExtension.Companion.logger.info { "RootScopeNode.afterTest ${rootScope}" }
    }
}

class ContainerScopeNode(val containerScope: ContainerScope) : Node {
    override val testCase: TestCase?
        get() = containerScope.testCase
    override val testPath: TestPath?
        get() = containerScope.testCase.descriptor.path()

    override fun beforeGroup(block: TestCase.() -> Unit) {
        containerScope.beforeContainer { testCase ->
            LetValueExtension.Companion.logger.info { "ContainerScopeNode.beforeGroup: ${testCase.descriptor.path()}" }
            block(testCase)
        }
    }

    override fun afterGroup(block: TestCase.() -> Unit) {
        containerScope.afterContainer { (testCase, testResult) ->
            LetValueExtension.Companion.logger.info { "ContainerScopeNode.afterGroup: ${testCase.descriptor.path()}" }
            block(testCase)
        }
    }

    override fun beforeTest(block: TestCase.() -> Unit) {
        containerScope.beforeTest { testCase ->
            LetValueExtension.Companion.logger.info { "ContainerScopeNode.beforeTest: ${testCase.descriptor.path()}" }
            block(testCase)
        }
    }

    override fun afterTest(block: TestCase.(TestResult) -> Unit) {
        containerScope.afterTest { (testCase, testResult) ->
            LetValueExtension.Companion.logger.info { "ContainerScopeNode.afterTest: ${testCase.descriptor.path()}" }
            block(testCase, testResult)
        }
    }
}

class LetValueCreator<T>(
    val factory: () -> T, val node: Node, val afterGroupDeclaration: NestLevel.(() -> Unit) -> Unit
) : LetValue.PropertyCreator<T> {
    init {
        LetValueExtension.Companion.logger.info { "LetValueCreator.init at ${node.testPath}" }
    }

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        return LetValueGetter(factory, node, property.name).also {
            val letValueGetter = it
            node.beforeGroup { letValueGetter.beforeExecuteGroup(this) }
            node.beforeTest { letValueGetter.beforeInvocation(this) }
            node.afterTest { letValueGetter.afterInvocation(this) }
            node.afterGroup { letValueGetter.afterExecuteGroup(this) }
        }
    }
}

class LetValueGetter<T>(baseFactory: () -> T, node: Node, val name: String) : ReadOnlyProperty<Any?, T> {
    private val paths = hashMapOf(node.testCase to baseFactory)
    private val stack = mutableListOf<TestCase?>()

    private var initializedForTest = false
    private var valueForTest: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        LetValueExtension.Companion.logger.info { "LetValueGetter.getValue: $name inTest = ${LetValuesState.inTest} current = ${LetValuesState.current}" }
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
        val fn = paths[searchPath]
            ?: throw IllegalStateException("no let value for ${LetValuesState.currentTestCase}")
        return fn().also {
            valueForTest = it
            initializedForTest = true
        }
    }

    fun override(node: Node, factory: () -> T) {
        LetValueExtension.Companion.logger.info { "LetValueGetter.override: $name for context = ${node.testPath}" }
        if (paths.containsKey(node.testCase)) {
            throw IllegalStateException("value already given for $name in $node")
        }
        paths[node.testCase] = factory
    }

    fun reset() {
        initializedForTest = false
        valueForTest = null
    }

    fun beforeExecuteGroup(group: TestCase) {
        stack.add(group)
//        currentTestCase = group
    }

    fun beforeInvocation(testCase: TestCase) {
//        LetValuesState.inTest = true
        LetValueExtension.Companion.logger.info { "LetValueGetter.beforeInvocation: $name, inTest := ${LetValuesState.inTest}" }
    }

    fun afterInvocation(testCase: TestCase) {
        reset()
        LetValueExtension.Companion.logger.info { "LetValueGetter.afterInvocation: $name, inTest := ${LetValuesState.inTest}" }
    }

    fun afterExecuteGroup(testCase: TestCase) {
        if (LetValuesState.current != null) {
            throw IllegalStateException("$name can't be used from beforeEachGroup or afterEachGroup")
        }
        stack.removeLast()
//        currentTestCase = stack.removeAt(stack.size - 1)
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