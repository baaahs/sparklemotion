@file:JsModule("lodash")

package external

external fun <T : Function<*>> throttle(fn: T, wait: Int): T