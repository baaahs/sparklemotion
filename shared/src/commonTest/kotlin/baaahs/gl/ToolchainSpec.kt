package baaahs.gl

import baaahs.describe
import baaahs.kotest.value
import baaahs.only
import baaahs.show.Shader
import io.kotest.core.spec.style.DescribeSpec

class ToolchainSpec : DescribeSpec({
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
                analysis.errors.only().message
                    .startsWith("huh? couldn't find a struct")
            }
        }
    }
})