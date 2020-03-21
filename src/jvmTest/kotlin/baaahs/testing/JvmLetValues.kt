package baaahs.testing

import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.runtime.Collector

actual fun getFinalizers(root: Root): MutableList<() -> Unit> {
    val finalizersField = Collector::class.java.getDeclaredField("finalizers")
    finalizersField.isAccessible = true
    return finalizersField.get(root) as MutableList<() -> Unit>
}