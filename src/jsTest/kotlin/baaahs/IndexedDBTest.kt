package baaahs

import kotlin.browser.window
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.expect

public class IndexedDBTest {
    @Ignore @Test
    fun objectStoreStuff() {
        val openDBRequest = window.indexedDB().open("testdb")
        openDBRequest.onsuccess = { event -> println(event) }
        openDBRequest.onerror = { event -> println(event) }

        expect(true) { false }
    }
}