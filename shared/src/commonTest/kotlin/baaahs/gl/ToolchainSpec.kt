package baaahs.gl

import baaahs.describe
import baaahs.kotest.value
import baaahs.only
import baaahs.show.Shader
import ch.tutteli.atrium.api.fluent.en_GB.startsWith
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object ToolchainSpec : DescribeSpec({
    describe<Toolchain> {
        val toolchain by value { RootToolchain(testPlugins()) }
        context("analyze") {
            val shaderText by value {
                Shader(
                    "Title",
                    """
                        struct Whatever {
                            float a;
                            float b;
                        }
                        void main() { ... };
                    """.trimIndent()
                )
            }

            it("catches analyzer exceptions") {
                val analysis = toolchain.analyze(shaderText)
                expect(analysis.errors.only().message)
                    .startsWith("huh? couldn't find a struct")
            }
        }
    }
})