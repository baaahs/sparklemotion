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
interface TestCoroutineScope: CoroutineScope/*, UncaughtExceptionCaptor, DelayController*/ {
    /**
     * Call after the test completes.
     * Calls [UncaughtExceptionCaptor.cleanupTestCoroutines] and [DelayController.cleanupTestCoroutines].
     *
     * @throws Throwable the first uncaught exception, if there are any uncaught exceptions.
     * @throws UncompletedCoroutinesError if any pending tasks are active, however it will not throw for suspended
     * coroutines.
     */
    fun cleanupTestCoroutines()
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
expect fun TestCoroutineScope(context: CoroutineContext = EmptyCoroutineContext): TestCoroutineScope
