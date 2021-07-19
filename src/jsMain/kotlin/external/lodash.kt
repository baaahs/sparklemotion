package external

@JsModule("lodash/throttle")
external fun <T : Function<*>> throttle(fn: T, wait: Int): T