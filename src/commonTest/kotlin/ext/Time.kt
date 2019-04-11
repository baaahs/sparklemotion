package ext

import kotlin.math.round
import kotlin.math.roundToLong

interface TimeUnit {
    val timeIntervalRatio: Double
    fun <OtherUnit : TimeUnit> conversionRate(otherTimeUnit: OtherUnit): Double {
        return timeIntervalRatio / otherTimeUnit.timeIntervalRatio
    }

    fun convert(l: Long, unit: TimeUnit): Long = (l * conversionRate(unit)).roundToLong()
}


class Interval<out T : TimeUnit>(value: Number, factory: () -> T) {

    val unit: T = factory()

    val value = value.toDouble()

    val longValue = round(this.value).toLong()

    val inDays: Interval<Day> get() = convertedTo(Day())
    val inHours: Interval<Hour> get() = convertedTo(Hour())
    val inMinutes: Interval<Minute> get() = convertedTo(Minute())
    val inSeconds: Interval<Second> get() = convertedTo(Second())
    val inMilliseconds: Interval<Millisecond> get() = convertedTo(Millisecond())
    val inMicroseconds: Interval<Microsecond> get() = convertedTo(Microsecond())
    val inNanoseconds: Interval<Nanosecond> get() = convertedTo(Nanosecond())

    fun <T : TimeUnit> convertedTo(timeUnit: T): Interval<T> {
        return Interval(value * unit.conversionRate(timeUnit)) { throw RuntimeException() }
    }

    operator fun plus(other: Interval<TimeUnit>): Interval<T> {
        val newValue = value + other.value * other.unit.conversionRate(unit)
        return Interval(newValue) { unit }
    }

    operator fun minus(other: Interval<TimeUnit>): Interval<T> {
        val newValue = value - other.value * other.unit.conversionRate(unit)
        return Interval(newValue) { unit }
    }

    operator fun times(other: Number): Interval<T> {
        return Interval(value * other.toDouble()) { unit }
    }

    operator fun div(other: Number): Interval<T> {
        return Interval(value / other.toDouble()) { unit }
    }

    operator fun inc() = Interval(value + 1) { unit }

    operator fun dec() = Interval(value - 1) { unit }

    operator fun compareTo(other: Interval<TimeUnit>) = inMilliseconds.value.compareTo(other.inMilliseconds.value)

    operator fun contains(other: Interval<TimeUnit>) = inMilliseconds.value >= other.inMilliseconds.value

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Interval<TimeUnit>) return false
        return compareTo(other) == 0
    }

    override fun hashCode() = inMilliseconds.value.hashCode()

    override fun toString(): String {
        val unitString = unit::class.simpleName?.toLowerCase()
        val isWhole = value % 1 == 0.0
        return (if (isWhole) longValue.toString() else value.toString())
            .plus(" ")
            .plus(if (value == 1.0) unitString else unitString.plus("s"))
    }
}


class Day : TimeUnit {
    override val timeIntervalRatio = 86400.0
}

class Hour : TimeUnit {
    override val timeIntervalRatio = 3600.0
}

class Minute : TimeUnit {
    override val timeIntervalRatio = 60.0
}

class Second : TimeUnit {
    override val timeIntervalRatio = 1.0
}

class Millisecond : TimeUnit {
    override val timeIntervalRatio = 0.001
}

class Microsecond : TimeUnit {
    override val timeIntervalRatio = 0.000001
}

class Nanosecond : TimeUnit {
    override val timeIntervalRatio = 1e-9
}


val Number.days: Interval<Day>
    get() = Interval(this) { Day() }

val Number.hours: Interval<Hour>
    get() = Interval(this) { Hour() }

val Number.minutes: Interval<Minute>
    get() = Interval(this) { Minute() }

val Number.seconds: Interval<Second>
    get() = Interval(this) { Second() }

val Number.milliseconds: Interval<Millisecond>
    get() = Interval(this) { Millisecond() }

val Number.microseconds: Interval<Microsecond>
    get() = Interval(this) { Microsecond() }

val Number.nanoseconds: Interval<Nanosecond>
    get() = Interval(this) { Nanosecond() }


