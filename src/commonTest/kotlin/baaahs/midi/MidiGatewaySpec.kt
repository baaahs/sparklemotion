package baaahs.midi

import baaahs.FakeClock
import baaahs.describe
import ch.tutteli.atrium.api.verbs.expect
import ext.kotlinx_coroutines_test.TestCoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.spekframework.spek2.Spek

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object MidiGatewaySpec : Spek({
    val clock by value { FakeClock() }
    val scope by value { TestCoroutineScope() }
    val server by value { MidiGatewayServer() }
    val client by value { MidiGatewayClient(server, scope) }
    val device1 by value { MidiDevice("midi1", "MIDI 1", "Fake", "Fake", "1.0") }

    describe<MidiGateway> {
        it("receives MIDI events") {
            client.receivedEvent(device1, MidiEvent(clock.now(), 1, 2, 3, 4))
            scope.runCurrent()
            expect(server.)
        }
    }
})