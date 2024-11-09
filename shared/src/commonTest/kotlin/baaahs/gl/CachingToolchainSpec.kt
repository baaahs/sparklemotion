package baaahs.gl

import baaahs.describe
import baaahs.kotest.value
import baaahs.show.Shader
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

object CachingToolchainSpec : DescribeSpec({
    describe<CachingToolchain> {
        val cache1 by value { CachingToolchain(testToolchain) }
        val someShader by value {
            Shader("Some Shader", "void main() { ... }")
        }

        it("sanity check that the root toolchain doesn't cache") {
            val analysis1 = testToolchain.analyze(someShader)
            val analysis2 = testToolchain.analyze(someShader)
            analysis1 shouldNotBeSameInstanceAs analysis2
        }

        it("caches Shader -> ShaderAnalysis") {
            val analysis1 = cache1.analyze(someShader)
            val analysis2 = cache1.analyze(someShader)
            analysis1 shouldBeSameInstanceAs analysis2
        }

        context("when there's another layer of caching") {
            val cache2 by value { CachingToolchain(cache1) }

            it("caches Shader -> ShaderAnalysis") {
                val analysis1 = cache1.analyze(someShader)
                val analysis2 = cache2.analyze(someShader)
                analysis1 shouldBeSameInstanceAs analysis2
            }

            it("caches at the outermost layer only") {
                val analysis2 = cache2.analyze(someShader)
                val analysis1 = cache1.analyze(someShader)
                analysis1 shouldNotBeSameInstanceAs analysis2
            }
        }

        context("pruneUnused") {
            it("removes entries that aren't accessed within the block") {
                val analysis1 = cache1.analyze(someShader)
                cache1.pruneUnused {
                    cache1.analyze(Shader("Some Other Shader", "void main() { ... }"))
                }
                val analysis2 = cache1.analyze(someShader)
                analysis1 shouldNotBeSameInstanceAs analysis2
            }
        }
    }
})