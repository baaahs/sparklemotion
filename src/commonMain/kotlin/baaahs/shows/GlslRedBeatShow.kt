package baaahs.shows

object GlslRedBeatShow : GlslShow("GlslRedBeatShow") {

    override val program = """

// Red on each beat, purple on each start of measure.
void main() {
    gl_FragColor = vec4(sm_beat, 0., sm_startOfMeasure, 1.);
}
    """.trimIndent()
}
