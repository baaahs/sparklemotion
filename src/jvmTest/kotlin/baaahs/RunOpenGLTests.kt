package baaahs

import baaahs.glshaders.GlslProgramSpec
import baaahs.glsl.GlslRendererTest
import baaahs.glsl.LwjglGlslManager
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter

fun main() {
    LwjglGlslManager().available

    val request: LauncherDiscoveryRequest =
        LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClass(GlslRendererTest::class.java),
                selectClass(GlslProgramSpec::class.java)
            )
            .build()
    val launcher = LauncherFactory.create()
    val testPlan = launcher.discover(request)
    val listener = SummaryGeneratingListener()
    launcher.registerTestExecutionListeners(listener)
    launcher.execute(testPlan, listener)

    listener.summary.printFailuresTo(PrintWriter(System.out))
    listener.summary.printTo(PrintWriter(System.out))

    if (listener.summary.failures.isNotEmpty()) {
        System.exit(1)
    }
}
