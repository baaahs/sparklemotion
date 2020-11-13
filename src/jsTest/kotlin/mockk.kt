import kotlin.reflect.KClass

actual inline fun <reified T : Any> mockk(
    name: String?,
    relaxed: Boolean,
    vararg moreInterfaces: KClass<*>,
    relaxUnitFun: Boolean,
    crossinline block: T.() -> Unit
): T =
    error("mockk not supported in JS")