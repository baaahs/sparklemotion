package baaahs.ui

import react.RMutableRef

@Suppress("UNCHECKED_CAST")
fun <T> noSuch(): T = null as T

@Suppress("UNCHECKED_CAST")
fun <T> useRef(): RMutableRef<T> = react.useRef(null as T)

@Suppress("UNCHECKED_CAST")
fun <T> useState() = react.useState(null as T)
