package baaahs

import org.junit.Assume

actual fun assumeTrue(boolean: Boolean) {
    Assume.assumeTrue(boolean)
}