package baaahs

import baaahs.gl.LwjglGlManager
import baaahs.gl.render.RenderEngineTest
import baaahs.gl.render.RenderManagerTest
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.EngineFilter
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter

val testsRequiringOpenGL = listOf(
    RenderEngineTest::class,
    RenderManagerTest::class
)

fun main() {
    LwjglGlManager().available

    val request: LauncherDiscoveryRequest =
        LauncherDiscoveryRequestBuilder.request()
            .filters(EngineFilter.excludeEngines("spek2"))
            .selectors(testsRequiringOpenGL.map { selectClass(it.java) })
            .build()
    val launcher = LauncherFactory.create()
    val testPlan = launcher.discover(request)
    val listener = SummaryGeneratingListener()
    launcher.registerTestExecutionListeners(listener, object : TestExecutionListener {
        override fun executionStarted(testIdentifier: TestIdentifier) {
            System.out.println("${testIdentifier.displayName} started\n")
        }

        override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {
            System.out.println("${testIdentifier.displayName} skipped${if (reason.isEmpty()) "" else ": $reason"}\n")
        }

        override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
            System.out.println("${testIdentifier.displayName} finished: ${testExecutionResult.status}\n")
        }
    })
    launcher.execute(testPlan, listener)

    listener.summary.printFailuresTo(PrintWriter(System.out))
    System.out.println("\n\n---\n\n")
    listener.summary.printTo(PrintWriter(System.out))

    if (listener.summary.failures.isNotEmpty()) {
        System.exit(1)
    }
}
