package baaahs

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

inline fun <reified T> DescribeSpec.describe(noinline test: suspend DescribeSpecContainerScope.() -> Unit) =
    describe(T::class.toString(), test)
