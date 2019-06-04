package ext

//@file:Suppress("PackageDirectoryMismatch")

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private const val MAX_DELAY = Long.MAX_VALUE / 2 // cannot delay for too long on Android

@InternalCoroutinesApi
@Suppress("unused")
class TestCoroutineContext constructor(private val name: String?) : CoroutineContext {
    private val handler = TestHandler()
    private val context: CoroutineContext
    private val caughtExceptions = mutableListOf<Throwable>()

    /**
     * Exceptions that were caught during a [launch] or a [async] + [Deferred.await].
     */
    val exceptions: List<Throwable> get() = caughtExceptions

    init {
        context = Dispatcher() + CoroutineExceptionHandler(this::handleException)
    }

    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R): R =
        context.fold(initial, operation)

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? = context[key]

    override fun minusKey(key: CoroutineContext.Key<*>) = context.minusKey(key)

    /**
     * @return The current virtual clock-time as it is known to this CoroutineContext
     */
    fun now(unit: TimeUnit = Millisecond()) = handler.now(unit)

    /**
     * Moves the CoroutineContext's virtual clock forward by a specified amount of time.
     *
     * @param delayTime
     * the amount of time to move the CoroutineContext's clock forward
     * @param unit
     * the units of time that [delayTime] is expressed in
     */
    fun advanceTimeBy(delayTime: Long, unit: TimeUnit = Millisecond()) {
        handler.advanceTimeBy(delayTime, unit)
    }

    /**
     * Moves the CoroutineContext's clock-time to a particular moment in time.
     *
     * @param delayTime
     * the point in time to move the CoroutineContext's clock to
     * @param unit
     * the units of time that [delayTime] is expressed in
     */
    fun advanceTimeTo(delayTime: Long, unit: TimeUnit = Millisecond()) {
        handler.advanceTimeTo(delayTime, unit)
    }

    /**
     * Triggers any actions that have not yet been triggered and that are scheduled to be triggered at or
     * before this CoroutineContext's present virtual time.
     */
    fun triggerActions() {
        handler.triggerActions()
    }

    /**
     * Cancels all not yet triggered actions. Be careful calling this, since it can seriously
     * mess with your coroutines work. This method should usually be called on tear-down of a
     * unit test.
     */
    fun cancelAllActions() {
        handler.cancelAllActions()
    }

    fun runAll() = handler.runAll()

    override fun toString() = name ?: handler.toString()
    override fun equals(other: Any?) = other is TestCoroutineContext && other.handler === handler
    override fun hashCode() = handler.hashCode()

    private fun handleException(@Suppress("UNUSED_PARAMETER") context: CoroutineContext, exception: Throwable) {
        caughtExceptions += exception
    }

    @InternalCoroutinesApi
    private inner class Dispatcher : CoroutineDispatcher(), Delay {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            handler.post(block)
        }

        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            handler.postDelayed(Runnable {
                with(continuation) { resumeUndispatched(Unit) }
            }, timeMillis.coerceAtMost(MAX_DELAY))
        }

        override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
            handler.postDelayed(block, timeMillis.coerceAtMost(MAX_DELAY))
            return object : DisposableHandle {
                override fun dispose() {
                    handler.removeCallbacks(block)
                }
            }
        }

//        override fun processNextEvent() = handler.processNextEvent()
    }
}

private class TestHandler {
    /** The ordered queue for the runnable tasks.  */
    private val queue = PriorityBlockingQueue<TimedRunnable>()
    /** The per-scheduler global order counter.  */
    /*@Volatile*/ private var counter = 0L
    // Storing time in nanoseconds internally.
    /*@Volatile*/ private var time = 0L
    private val nanosecond = Nanosecond()

    private val nextEventTime get() = if (queue.isEmpty()) Long.MAX_VALUE else 0L

    internal fun post(block: Runnable) {
        val run = TimedRunnable(block, counter++)
        queue.add(run)
    }

    internal fun postDelayed(block: Runnable, delayTime: Long) {
        val run = TimedRunnable(block, counter++, time + delayTime.milliseconds.inNanoseconds.longValue)
        queue.add(run)
    }

    internal fun removeCallbacks(block: Runnable) {
        queue.remove(TimedRunnable(block))
    }

    internal fun now(unit: TimeUnit) = time.nanoseconds.convertedTo(unit)

    internal fun advanceTimeBy(delayTime: Long, unit: TimeUnit): Long {
        val oldTime = time
        advanceTimeTo(time + unit.convert(delayTime, nanosecond), nanosecond)
        return unit.convert(time - oldTime, nanosecond)
    }

    internal fun advanceTimeTo(delayTime: Long, unit: TimeUnit) {
        val targetTime = unit.convert(delayTime, nanosecond)
        triggerActions(targetTime)

        if (targetTime > time) {
            time = targetTime
        }
    }

    internal fun triggerActions() {
        triggerActions(time)
    }

    internal fun cancelAllActions() {
        queue.clear()
    }

    internal fun processNextEvent(): Long {
        val current = queue.peek()
        if (current != null) {
            /** Automatically advance time for [EventLoop]-callbacks */
            triggerActions(current.time)
        }

        return nextEventTime
    }

    private fun triggerActions(targetTime: Long) {
        while (true) {
            val current = queue.peek()
            if (current == null || current.time > targetTime) {
                break
            }
            // If the scheduled time is 0 (immediate) use current virtual time
            time = if (current.time == 0L) time else current.time

            queue.remove(current)
            current.run()
        }
    }

    fun runAll() {
        while (!queue.isEmpty()) {
            advanceTimeTo(queue.peek()!!.time, nanosecond)
        }
    }
}

class PriorityBlockingQueue<T : Comparable<T>> {
    val items: MutableList<T?> = mutableListOf()

    fun add(t: T) {
        items.add(t)
        sort()
    }

    fun peek() = if (items.isEmpty()) null else items[0]

    fun remove(t: T) = items.remove(t)

    fun clear() = items.clear()

    fun isEmpty() = items.isEmpty()

    private fun sort() {
        items.sortWith(Comparator { a, b -> a!!.compareTo(b!!) })
    }
}

private class TimedRunnable(
    private val run: Runnable,
    private val count: Long = 0,
    internal val time: Long = 0 // nanos
) : Comparable<TimedRunnable>, Runnable {
    override fun run() {
        run.run()
    }

    override fun compareTo(other: TimedRunnable) = if (time == other.time) {
        count.compareTo(other.count)
    } else {
        time.compareTo(other.time)
    }

    override fun hashCode() = run.hashCode()
    override fun equals(other: Any?) = other is TimedRunnable && (run == other.run)

    override fun toString() = "TimedRunnable(time = $time, run = $run)"
}