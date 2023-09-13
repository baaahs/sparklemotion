package baaahs.plugin.beatlink

import baaahs.gl.RootToolchain
import baaahs.gl.kexpect
import baaahs.gl.openShader
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.plugin.ClientPlugins
import baaahs.plugin.Plugins
import baaahs.plugin.core.CorePlugin
import baaahs.show.Shader
import baaahs.show.mutable.MutablePatchSet
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object WaveformFeedSpec : Spek({
    describe("Waveform feed") {
        val shaderText by value {
            """
                struct PlayerState {
                    float trackStartTime;
                    float trackLength;
                };
        
                struct PlayerData {
                    PlayerState players[4];
                };
                uniform PlayerData playerData; // @@baaahs.BeatLink:PlayerData
                
                uniform sampler2D waveforms[4]; // @@baaahs.BeatLink:Waveforms
        
                vec4 drawOneWaveform(sampler2D waveform, PlayerState playerState, vec2 pos) {
                    return texture(waveform, pos);
                }
        
                vec4 drawWaveform(vec2 pos) {
                    vec4 waveformValue;
                    if (pos.y < .25) {
                        waveformValue = drawOneWaveform(waveforms[0], playerData.players[0], pos);
                    } else if (pos.y < .5) {
                        waveformValue = drawOneWaveform(waveforms[1], playerData.players[0], pos);
                    } else if (pos.y < .75) {
                        waveformValue = drawOneWaveform(waveforms[2], playerData.players[0], pos);
                    } else {
                        waveformValue = drawOneWaveform(waveforms[3], playerData.players[0], pos);
                    }
                    return waveformValue;
                }
        
                void main(void) {
                    gl_FragColor = drawWaveform(gl_FragCoord.xy);
                }
            """.trimIndent()
        }

        val plugins by value {
            val context = Plugins.dummyContext
            ClientPlugins(
                listOf(CorePlugin.openSafe(context))
                        + BeatLinkPlugin(FakeBeatSource(), context),
                context
            )
        }

        val autoWirer by value { AutoWirer(plugins) }
        val toolchain by value { RootToolchain(plugins, autoWirer = autoWirer) }
        val openShader by value { toolchain.openShader(Shader("waveform visualizer", shaderText)) }
        val unresolvedPatch by value { toolchain.autoWire(openShader) }
        val glsl by value {
            unresolvedPatch.acceptSuggestedLinkOptions().confirm()
                .let { MutablePatchSet(it) }
                .openForPreview(toolchain, ContentType.Color)!!
                .toGlsl().trim()
        }

        context("annoyingly, webgl forbids sampler2Ds inside structs") {
            it("suggests links") {
                println("incomingLinksOptions: ${unresolvedPatch.incomingLinksOptions}")
                unresolvedPatch.incomingLinksOptions.forEach {
                    println("it = ${it}")
                }
            }

            it("generates GLSL") {
                kexpect(glsl).toBe(
                    /**language=glsl*/
                    """
                        #ifdef GL_ES
                        precision mediump float;
                        #endif

                        // SparkleMotion-generated GLSL

                        layout(location = 0) out vec4 sm_result;
                        
                        struct PlayerState {
                            float trackStartTime;
                            float trackLength;
                        };

                        struct PlayerData {
                            PlayerState[4] players;
                        };

                        struct p0_waveformVisualizer_PlayerData {
                            PlayerState players[4];
                        };
                        
                        // Feed: Player Data
                        uniform PlayerData in_playerData;

                        // Feed: Player Waveforms
                        uniform sampler2D[4] in_playerWaveforms;

                        // Shader: waveform visualizer; namespace: p0
                        // waveform visualizer

                        vec4 p0_waveformVisualizer_gl_FragColor = vec4(0., 0., 0., 1.);

                        #line 13 0
                        vec4 p0_waveformVisualizer_drawOneWaveform(sampler2D waveform, PlayerState playerState, vec2 pos) {
                            return texture(waveform, pos);
                        }

                        #line 17 0
                        vec4 p0_waveformVisualizer_drawWaveform(vec2 pos) {
                            vec4 waveformValue;
                            if (pos.y < .25) {
                                waveformValue = p0_waveformVisualizer_drawOneWaveform(in_playerWaveforms[0], in_playerData.players[0], pos);
                            } else if (pos.y < .5) {
                                waveformValue = p0_waveformVisualizer_drawOneWaveform(in_playerWaveforms[1], in_playerData.players[0], pos);
                            } else if (pos.y < .75) {
                                waveformValue = p0_waveformVisualizer_drawOneWaveform(in_playerWaveforms[2], in_playerData.players[0], pos);
                            } else {
                                waveformValue = p0_waveformVisualizer_drawOneWaveform(in_playerWaveforms[3], in_playerData.players[0], pos);
                            }
                            return waveformValue;
                        }

                        #line 31 0
                        void p0_waveformVisualizer_main(void) {
                            p0_waveformVisualizer_gl_FragColor = p0_waveformVisualizer_drawWaveform(vec2(0.).xy);
                        }


                        #line 10001
                        void main() {
                            // Invoke waveform visualizer
                            p0_waveformVisualizer_main();

                            sm_result = p0_waveformVisualizer_gl_FragColor;
                        }
                    """.trimIndent()
                )
            }
        }
    }
})