package baaahs.plugin.core.feed

import baaahs.FakeClock
import baaahs.describe
import baaahs.geom.Vector4F
import baaahs.gl.override
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.dialect.IsfShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.glsl.GlslUniform
import baaahs.glsl.TextureUniform
import baaahs.kotest.value
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.shows.FakeUniform
import baaahs.shows.StubGlslProgram
import baaahs.toBeSpecified
import com.danielgergely.kgl.Kgl
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import kotlinx.datetime.Instant

class DateFeedSpec : DescribeSpec({
    describe<DateFeed> {

        val builder by value { DateFeed }
        val inputPort by value { toBeSpecified<InputPort>() }
        val feed by value { builder.build(inputPort) }
        val time by value { Instant.fromEpochMilliseconds(1619099367792) } // 2021-04-22 00:13:49.793 UTC
        val uniform by value { FakeUniform() }
        val feedContext by value {
            val showPlayer = FakeShowPlayer().also {
                (it.toolchain.plugins.pluginContext.clock as FakeClock).time = time
            }
            val gl = FakeGlContext()
            val fakeProgram = object : StubGlslProgram() {
                override fun getUniform(name: String): GlslUniform = uniform
                override fun getTextureUniform(name: String): TextureUniform? = null
                override fun <T> withProgram(fn: Kgl.() -> T): T = fn(gl.fakeKgl)
            }

            feed.open(showPlayer, "date")
                .bind(gl)
                .bind(fakeProgram)
        }

        context("for ShaderToy shaders") {
            override(inputPort) { ShaderToyShaderDialect.wellKnownInputPorts.find { it.id == "iDate" } }

            it("builds a feed with zero-based months and one-based days") {
                feed.shouldBe(DateFeed(zeroBasedMonth = true, zeroBasedDay = false))
            }

            it("sets correct uniform values") {
                feedContext.setOnProgram()
                uniform.value.toString()
                    .shouldBe(Vector4F(2021f, 3f, 22f, 35367.793f).toString())
            }
        }

        context("for ISF shaders") {
            override(inputPort) { IsfShaderDialect.wellKnownInputPorts.find { it.id == "DATE" } }

            it("builds a feed with zero-based months and zero-based days") {
                feed.shouldBe(DateFeed(zeroBasedMonth = false, zeroBasedDay = false))
            }

            it("sets correct uniform values") {
                feedContext.setOnProgram()
                uniform.value.toString()
                    .shouldBe(Vector4F(2021f, 4f, 22f, 35367.793f).toString())
            }
        }
    }
})