package baaahs.show.mutable

import baaahs.describe
import baaahs.gl.override
import baaahs.gl.patch.AutoWirer
import baaahs.gl.preview.ShaderBuilder
import baaahs.glsl.Shaders
import baaahs.only
import baaahs.plugin.Plugins
import baaahs.show.live.wireUp
import baaahs.show.mutable.EditingShader.State
import baaahs.ui.Observer
import baaahs.ui.addObserver
import io.mockk.*
import org.spekframework.spek2.Spek
import kotlin.test.expect

// Currently in jvmTest so we can use mockk.
// TODO: move back to commonTest when mockk supports multiplatform.
object EditingShaderSpec : Spek({
    describe<EditingShader> {
        val autoWirer by value { AutoWirer(Plugins.safe()) }
        val mutablePatch by value { autoWirer.wireUp(Shaders.ripple) }
        val mutableShow by value {
            MutableShow("show") { addPatch(mutablePatch) }
        }
        val mutableShaderInstance by value { mutablePatch.mutableShaderInstances.only() }
        val observerSlot by value { slot<Observer>() }
        val mockShaderBuilder by value { mockk<ShaderBuilder>() }
        beforeEachTest {
            every { mockShaderBuilder.addObserver(capture(observerSlot)) } answers { observerSlot.captured }
            every { mockShaderBuilder.startBuilding() } just runs
            every { mockShaderBuilder.gadgets } returns emptyList()
        }
        val notifiedStates by value { arrayListOf<State>() }

        val editingShader by value {
            EditingShader(mutableShow, mutablePatch, mutableShaderInstance) {
                mockShaderBuilder
            }.also { it.addObserver { notifiedStates.add(it.state) } }
        }

        context("at initialization") {
            beforeEachTest {
                editingShader.let {} // Make sure it's warmed up.
            }

            it("is in Building state") {
                expect(State.Building) { editingShader.state }
            }

            it("starts building the shader") {
                verify { mockShaderBuilder.startBuilding() }
            }

            it("has no gadgets") {
                expect(emptyList()) { editingShader.gadgets }
            }

            it("should not have notified observers yet") {
                expect(emptyList<State>()) { notifiedStates }
            }

            context("if shader builder notifies us") {
                val builderState by value { ShaderBuilder.State.Success }

                beforeEachTest {
                    every { mockShaderBuilder.state } returns builderState
                    observerSlot.captured.notifyChanged()
                }

                context("of a success") {
                    it("should notify our observers") {
                        expect(listOf(State.Success)) { notifiedStates }
                    }
                }

                context("that it's still building") {
                    override(builderState) { ShaderBuilder.State.Compiling }
                    it("should not notify our observers") {
                        expect(emptyList<State>()) { notifiedStates }
                    }
                }

                context("of a failure") {
                    override(builderState) { ShaderBuilder.State.Errors }
                    it("should not notify our observers again") {
                        expect(listOf(State.Errors)) { notifiedStates }
                    }
                }
            }
        }
    }
})