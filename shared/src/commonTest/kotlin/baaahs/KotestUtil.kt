package baaahs

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope

@OptIn(ExperimentalKotest::class)
inline fun <reified T> DescribeSpec.describe() =
    describe(T::class.toString())

inline fun <reified T> DescribeSpec.describe(noinline test: suspend DescribeSpecContainerScope.() -> Unit) =
    describe(T::class.toString(), test)
