package ext.kotlinx_coroutines_test

/*
 * Copyright 2016-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A scope which provides detailed control over the execution of coroutines for tests.
 */
@ExperimentalCoroutinesApi // Since 1.2.1, tentatively till 1.3.0
class TestCoroutineScope(
    override val coroutineContext: CoroutineContext
): CoroutineScope,
    UncaughtExceptionCaptor by coroutineContext.uncaughtExceptionCaptor,
    DelayController by coroutineContext.delayController
{
    fun cleanupTestCoroutines() {
        coroutineContext.uncaughtExceptionCaptor.cleanupTestCoroutinesFromUncaught()
        coroutineContext.delayController.cleanupTestCoroutinesFromDelay()
    }
}

/**
 * A scope which provides detailed control over the execution of coroutines for tests.
 *
 * If the provided context does not provide a [ContinuationInterceptor] (Dispatcher) or [CoroutineExceptionHandler], the
 * scope adds [TestCoroutineDispatcher] and [TestCoroutineExceptionHandler] automatically.
 *
 * @param context an optional context that MAY provide [UncaughtExceptionCaptor] and/or [DelayController]
 */
@InternalCoroutinesApi
@Suppress("FunctionName")
@ExperimentalCoroutinesApi // Since 1.2.1, tentatively till 1.3.0
public fun createTestCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): TestCoroutineScope {
    var safeContext = context
    if (context[ContinuationInterceptor] == null) safeContext += TestCoroutineDispatcher()
    if (context[CoroutineExceptionHandler] == null) safeContext += TestCoroutineExceptionHandler()
    return TestCoroutineScope(safeContext)
}

private inline val CoroutineContext.uncaughtExceptionCaptor: UncaughtExceptionCaptor
    get() {
        val handler = this[CoroutineExceptionHandler]
        return handler as? UncaughtExceptionCaptor ?: throw IllegalArgumentException(
            "TestCoroutineScope requires a UncaughtExceptionCaptor such as " +
                    "TestCoroutineExceptionHandler as the CoroutineExceptionHandler"
        )
    }

private inline val CoroutineContext.delayController: DelayController
    get() {
        val handler = this[ContinuationInterceptor]
        return handler as? DelayController ?: throw IllegalArgumentException(
            "TestCoroutineScope requires a DelayController such as TestCoroutineDispatcher as " +
                    "the ContinuationInterceptor (Dispatcher)"
        )
    }