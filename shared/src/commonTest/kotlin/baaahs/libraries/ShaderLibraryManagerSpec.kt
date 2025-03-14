package baaahs.libraries

import baaahs.FakePubSub
import baaahs.describe
import baaahs.gl.RootToolchain
import baaahs.gl.testPlugins
import baaahs.io.FsServerSideSerializer
import baaahs.kotest.value
import baaahs.sim.FakeFs
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object ShaderLibraryManagerSpec : DescribeSpec({
    describe< ShaderLibraryManager> {
        val plugins by value { testPlugins() }
        val toolchain by value { RootToolchain(plugins) }
        val fs by value { FakeFs() }
        val pubSub by value { FakePubSub() }
        val shaderLibraryManager by value {
            ShaderLibraryManager(plugins, fs, FsServerSideSerializer(), pubSub.server, toolchain)
        }

        beforeEach {
            fs.saveFile(
                fs.resolve("shader-libraries/testlib/_libraryIndex.json"),
                /** language=json */
                """
                  {
                    "title": "Built-In",
                    "description": "Some built-in shaders!",
                    "license": null,
                    "entries": [
                      {
                        "id": "acid-at-the-disco",
                        "title": "Acid at the Disco",
                        "description": "A cool shader!",
                        "lastModifiedMs": 0,
                        "tags": [
                          "@type=paint",
                          "Disco"
                        ],
                        "srcFile": "shaders/shader1.glsl"
                      },
                      {
                        "id": "ripple",
                        "title": "Ripple",
                        "description": "A nifty shader!",
                        "lastModifiedMs": 0,
                        "tags": [
                          "@type=distort",
                          "@filter"
                        ],
                        "srcFile": "shaders/shader2.glsl"
                      }
                    ]
                  }
                """.trimIndent()
            )
            fs.saveFile(fs.resolve("shader-libraries/testlib/shaders/shader1.glsl"), "")
            fs.saveFile(fs.resolve("shader-libraries/testlib/shaders/shader2.glsl"), "")
        }

        beforeEach {
            shaderLibraryManager.start()
        }

        describe("tagList") {
            it("returns the list of all tags in shader libraries") {
                shaderLibraryManager.tagList().map { it.fullString }.toSet() shouldBe setOf(
                    "@type=paint", "@filter", "@type=distort", "Disco",
                )
            }
        }

        describe("searchFor") {
            it("matches terms in shader description") {
                shaderLibraryManager.searchFor("nifty").map { it.id } shouldBe listOf(
                    "built-in:ripple"
                )
            }

            it("matches shader tags") {
                shaderLibraryManager.searchFor("@type=paint").map { it.id } shouldBe listOf(
                    "built-in:acid-at-the-disco"
                )
            }

            it("treats terms as ANDed by default") {
                shaderLibraryManager.searchFor("acid @type=paint").map { it.id } shouldBe listOf(
                    "built-in:acid-at-the-disco"
                )

                shaderLibraryManager.searchFor("acid @type=distort").map { it.id } shouldBe emptyList()
            }

            it("excludes terms preceded by a minus") {
                shaderLibraryManager.searchFor("-nifty").map { it.id } shouldBe listOf(
                    "built-in:acid-at-the-disco"
                )
            }

            it("excludes tags preceded by a minus") {
                shaderLibraryManager.searchFor("-@type=paint").map { it.id } shouldBe listOf(
                    "built-in:ripple"
                )
            }
        }
    }
})