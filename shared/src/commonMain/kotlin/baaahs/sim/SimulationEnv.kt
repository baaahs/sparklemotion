package baaahs.sim

import kotlin.reflect.KClass

class SimulationEnv(build: Builder.() -> Unit) {
    private val components = hashMapOf<KClass<*>, Any>()
        .also { build.invoke(Builder(it)) }
        .toMap()

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(klass: KClass<T>): T {
        return components[klass] as? T
            ?: error("No ${klass.simpleName} component.")
    }

    class Builder(val map: MutableMap<KClass<*>, Any>) {
        inline fun <reified T: Any> component(component: T) {
            map[T::class] = component
        }
    }
}