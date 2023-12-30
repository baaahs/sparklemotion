package ext.kotlinx_coroutines_test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
actual fun TestCoroutineScope(context: CoroutineContext): TestCoroutineScope {
    val scope = kotlinx.coroutines.test.TestCoroutineScope(context)
    return object : TestCoroutineScope {
        override fun cleanupTestCoroutines() {
            scope.cleanupTestCoroutines()
        }

        override val coroutineContext: CoroutineContext
            get() = scope.coroutineContext
    }
}