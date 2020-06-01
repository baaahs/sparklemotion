package baaahs.ui

import react.RMutableRef

@Suppress("UNCHECKED_CAST")
fun <T> nuffin(): T = null as T

@Suppress("UNCHECKED_CAST")
fun <T> useRef(): RMutableRef<T> = react.useRef(null as T)
