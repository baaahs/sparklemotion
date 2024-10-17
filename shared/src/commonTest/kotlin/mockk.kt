import kotlin.reflect.KClass

expect inline fun <reified T : Any> mockk(
    name: String? = null,
    relaxed: Boolean = false,
    vararg moreInterfaces: KClass<*>,
    relaxUnitFun: Boolean = false,
    crossinline block: T.() -> Unit = {}
): T
