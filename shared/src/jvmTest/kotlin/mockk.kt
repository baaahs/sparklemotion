import kotlin.reflect.KClass

actual inline fun <reified T : Any> mockk(
    name: String?,
    relaxed: Boolean,
    vararg moreInterfaces: KClass<*>,
    relaxUnitFun: Boolean,
    crossinline block: T.() -> Unit
): T =
    io.mockk.mockk(
        name, relaxed, *moreInterfaces, relaxUnitFun = relaxUnitFun, block = block
    )
