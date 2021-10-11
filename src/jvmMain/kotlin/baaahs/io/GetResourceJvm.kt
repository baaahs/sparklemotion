package baaahs.io

import baaahs.Pinky

actual fun getResource(name: String): String {
    return Pinky::class.java.classLoader.getResource(name).readText()
}