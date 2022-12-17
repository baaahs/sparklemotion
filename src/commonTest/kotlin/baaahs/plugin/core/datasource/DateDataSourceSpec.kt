package baaahs.plugin.core.datasource

import baaahs.FakeClock
import baaahs.describe
import baaahs.geom.Vector4F
import baaahs.gl.override
import baaahs.gl.shader.InputPort
import baaahs.gl.shader.dialect.IsfShaderDialect
import baaahs.gl.shader.dialect.ShaderToyShaderDialect
import baaahs.glsl.Uniform
import baaahs.shows.FakeGlContext
import baaahs.shows.FakeShowPlayer
import baaahs.shows.FakeUniform
import baaahs.shows.StubGlslProgram
import baaahs.toBeSpecified
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import com.danielgergely.kgl.Kgl
import org.spekframework.spek2.Spek

object DateDataSourceSpec : Spek({
    describe<DateFeed> {

        val builder by value { DateFeed }
        val inputPort by value { toBeSpecified<InputPort>() }
        val dataSource by value { builder.build(inputPort) }
        val time by value { 1619099367.792 } // 2021-04-22 00:13:49.793 UTC
        val uniform by value { FakeUniform() }
        val feed by value {
            val showPlayer = FakeShowPlayer().also {
                (it.toolchain.plugins.pluginContext.clock as FakeClock).time = time
            }
            val gl = FakeGlContext()
            val fakeProgram = object : StubGlslProgram() {
                override fun getUniform(name: String): Uniform = uniform
                override fun <T> withProgram(fn: Kgl.() -> T): T = fn(gl.fakeKgl)
            }

            dataSource.open(showPlayer, "date")
                .bind(gl)
                .bind(fakeProgram)
        }

        context("for ShaderToy shaders") {
            override(inputPort) { ShaderToyShaderDialect.wellKnownInputPorts.find { it.id == "iDate" } }

            it("builds a datasource with zero-based months and one-based days") {
                expect(dataSource).toEqual(DateFeed(zeroBasedMonth = true, zeroBasedDay = false))
            }

            it("sets correct uniform values") {
                feed.setOnProgram()
                expect(uniform.value.toString())
                    .toEqual(Vector4F(2021f, 3f, 22f, 49767.793f).toString())
            }
        }

        context("for ISF shaders") {
            override(inputPort) { IsfShaderDialect.wellKnownInputPorts.find { it.id == "DATE" } }

            it("builds a datasource with zero-based months and zero-based days") {
                expect(dataSource).toEqual(DateFeed(zeroBasedMonth = false, zeroBasedDay = false))
            }


            it("sets correct uniform values") {
                feed.setOnProgram()
                expect(uniform.value.toString())
                    .toEqual(Vector4F(2021f, 4f, 22f, 49767.793f).toString())
            }
        }
    }
})