package external.lodash

@JsModule("lodash/throttle")
external fun <T : Function<*>> throttle(fn: T, wait: Int): T

@JsModule("lodash.isequal")
external fun isEqual(value: Any?, other: Any?): Boolean