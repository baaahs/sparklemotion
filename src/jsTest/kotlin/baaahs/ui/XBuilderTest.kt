package baaahs.ui

import kotlinext.js.jsObject
import react.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class XBuilderTest {
    lateinit var props: TestProps
    lateinit var log: MutableList<String>

    @BeforeTest
    internal fun setUp() {
        _react = FakeReact()
        props = jsObject()
        props.str = "a string"

        log = mutableListOf<String>()
    }

    @Test
    fun testPropsFloat() {
        render(xComponent<TestProps>("x") { props ->
            log.add("str is ${props.str}")
        }, props)

        expect(listOf("sxtr is a string")) { log }
    }

}

external interface TestProps : RProps {
    var str: String
}
fun <T> render(component: FunctionalComponent<T>) {
    (_react as FakeReact).context = RenderContext()
}

class RenderContext {
    val stuff = mutableListOf<Any>()
}

class FakeReact : PluggableReact {
    lateinit var context: RenderContext

    override fun buildElements(handler: RBuilder.() -> Unit): dynamic {
        return Unit
    }

    override fun useEffectWithCleanup(dependencies: RDependenciesList?, effect: () -> RCleanup) {
        context.
    }

    override fun <T> useMemo(callback: () -> T, dependencies: RDependenciesArray): T {
        TODO("not implemented")
    }

    override fun <T> useRef(initialValue: T): RMutableRef<T> {
        TODO("not implemented")
    }

    override fun <T> useState(valueInitializer: () -> T): Pair<T, RSetState<T>> {
        TODO("not implemented")
    }
}