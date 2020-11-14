package baaahs.util

typealias Time = Double

interface Clock {
    fun now(): Time
}