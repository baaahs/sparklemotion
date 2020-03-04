package baaahs

import kotlin.test.fail

actual fun assumeTrue(boolean: Boolean) {
    if (!boolean) fail("assumption failed!")
}