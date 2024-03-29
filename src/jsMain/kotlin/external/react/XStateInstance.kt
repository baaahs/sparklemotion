@file:Suppress("NOTHING_TO_INLINE")

package external.react

import kotlin.reflect.KProperty

// TODO: make external in IR
class StateSetter<T>
private constructor() {
    inline operator fun invoke(value: T) {
        asDynamic()(value)
    }

    inline operator fun invoke(noinline transform: (T) -> T) {
        asDynamic()(transform)
    }
}

@Deprecated(
    message = "Legacy name for `StateSetter`",
    replaceWith = ReplaceWith("StateSetter<T>"),
)
typealias RSetState<T> = StateSetter<T>

/**
 * Only works inside [fc]
 * @see <a href="https://reactjs.org/docs/hooks-state.html#hooks-and-function-components">Hooks and Function Components</a>
 */
// TODO: make external in IR
class XStateInstance<T>
private constructor() {
    inline operator fun component1(): T = asDynamic()[0]
    inline operator fun component2(): StateSetter<T> = asDynamic()[1]

    inline operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ): T =
        asDynamic()[0]

    inline operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        value: T,
    ) {
        asDynamic()[1](value)
    }
}

@Deprecated(
    message = "Legacy name for `XStateInstance`",
    replaceWith = ReplaceWith("XStateInstance<T>"),
)
typealias StateDelegate<T> = XStateInstance<T>
