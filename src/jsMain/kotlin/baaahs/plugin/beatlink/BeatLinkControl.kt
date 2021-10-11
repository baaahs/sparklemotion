package baaahs.plugin.beatlink

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.model.ModelInfo
import baaahs.show.Shader
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.percent
import kotlinx.css.*
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.HTMLElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import kotlin.math.roundToInt

/**language=glsl*/
private val beatLinkShaderGlsl = """precision mediump float;

    #define PI 3.14159

    uniform float time;
    uniform vec2 resolution;
    
    struct RawBeatInfo {
        float measureStartTime;
        float beatIntervalMs;
        float bpm;
        float beatsPerMeasure;
        float confidence;
    };
    uniform RawBeatInfo rawBeatInfo;// @@baaahs.BeatLink:RawBeatInfo

    float secPerBeat = rawBeatInfo.beatIntervalMs / 1000.;

    const vec2 beatsBottomLeft = vec2(.1, .1);
    const vec2 beatsTopRight = vec2(.9, .9);
    const vec2 beatsDimen = beatsTopRight - beatsBottomLeft;

    const vec3 backgroundColor = vec3(0.1, .6, 0.);
    const vec3 beatPowerColor = vec3(0., 0., .5);
    const vec3 beatIntensityColor = vec3(1., 0., .0);

    const vec4 nowBeatIntensityColor = vec4(0., 1., 0., 1.);
    const vec3 nowBeatWideBandColor = vec3(1., 1., 0.);
    const vec3 borderColor = vec3(.4, 0., 0.);

    float beatIntensity_(float power) {
        return clamp(
            pow(sin((power * 1. + .55) * PI), 4.) * 1.25 + .0,
            0., 1.
        );
    }

    float eq(float a, float b) {
        if (a == b) {
            return 1.;
        } else {
            return 0.;
        }
    }

    float not(float v) {
        return 1. - v;
    }

    float rect(vec2 bottomLeft, vec2 topRight, vec2 pos) {
        if (step(pos, beatsBottomLeft) == vec2(0.) && step(beatsTopRight, pos) == vec2(0.)) {
            return 1.;
        } else {
            return 0.;
        }
    }

    vec3 background(vec2 pos) {
        return backgroundColor - vec3(not(pos.y) * .5);
    }

    float timeInMeasure;
    float beatInMeasure;
    float timeInBeat;
    float nowPower;
    float nowBeatIntensity;

    vec4 drawBeats(vec2 pos) {
        float pX = pos.x * rawBeatInfo.beatsPerMeasure;
        float pY = pos.y;

        float linearBeatPower = 1. - mod(pX, 1.);
        float linearBeatIntensity = beatIntensity_(linearBeatPower);

        vec3 color = vec3(0.);
        float o = 1.;

        // Beat-tracking bar, wide channel.
        float nowBeatWideBandMarker = step(abs(pX - nowPower - beatInMeasure), .1);

        // Beat-tracking bar, red slider LED.
        float wideBandHotLine = nowBeatWideBandMarker * step(abs(pY - nowBeatIntensity), .025);
        color += o * wideBandHotLine * beatIntensityColor.rgb;
        o -= o * wideBandHotLine;

        // Beat-tracking bar, gooey indicator center.
        float nowBeatXBand = step(abs(pX - nowPower - beatInMeasure), .04);
        float nowBeatIntensityMarker = nowBeatXBand * nowBeatIntensityColor.a;
        color += o * nowBeatIntensityMarker * nowBeatIntensityColor.rgb * step(pY, nowBeatIntensity);
        o -= o * nowBeatIntensityMarker;

        color += o * nowBeatWideBandColor * nowBeatWideBandMarker;
        o -= o * nowBeatWideBandMarker;

        // Sine wave-ish intensity preview.
        float beatIntensityBand = 1. - smoothstep(1.0 - distance(1.-linearBeatIntensity + pY - .5, .5), 1.0, 0.99);
        color += o * beatIntensityBand * beatIntensityColor;
        o -= o * beatIntensityBand;

        // Beat sawtooth.
        float beatMountain = step(pY, linearBeatPower);
        float beatMountainHighlight = not(nowBeatIntensity) * eq(beatInMeasure, floor(pX));
        color += o * beatMountain * (beatPowerColor + beatMountainHighlight);
        o -= o * beatMountain;

        // Background.
        color += o * background(pos);

        return vec4(color, 1.);
    }

    void main(void) {
        vec2 pos = gl_FragCoord.xy / resolution.xy;
        timeInMeasure = mod((time - rawBeatInfo.measureStartTime) / secPerBeat, rawBeatInfo.beatsPerMeasure);
        beatInMeasure = floor(timeInMeasure);
        timeInBeat = mod(timeInMeasure, 1.);
        nowPower = timeInBeat;
        nowBeatIntensity = beatIntensity_(nowPower);
    
        vec3 color = vec3(0.);
        float o = 1.;

        // Draw border.
        float border = 1. - rect(beatsBottomLeft, beatsTopRight, pos);
        color += border * borderColor * nowBeatIntensity;
        o -= o * border;
    
        // Draw beats.
        color += o * drawBeats((pos - beatsBottomLeft) / beatsDimen).rgb;

        gl_FragColor = vec4(color, 1.);
    }
""".trimIndent()
private val beatLinkVisualizerShader = Shader("BeatLink Visualizer", beatLinkShaderGlsl)

private val beatLinkControl = xComponent<BeatLinkControlProps>("BeatLinkControl") { _ ->
    val appContext = useContext(appContext)
    val clock = appContext.clock
    val beatSource = appContext.plugins.findPlugin<BeatLinkPlugin>().beatSource

    val bpmDiv = ref<HTMLElement>()
    val confidenceDiv = ref<HTMLElement>()

    val previewShaderBuilder = memo {
        PreviewShaderBuilder(beatLinkVisualizerShader, appContext.toolchain, ModelInfo.Empty)
    }

    fun update(beatData: BeatData) {
        val bpm = beatData.bpm
        bpmDiv.current!!.innerText = "${bpm.roundToInt()} BPM"

        val beatConfidence = beatData.confidence
        confidenceDiv.current!!.innerText = "Confidence: ${beatConfidence.percent()}%"
    }

    val setUniforms by handler { beatData: BeatData ->
        println("feeds = ${previewShaderBuilder.feeds}")
    }

    onMount {
        val observer = beatSource.addObserver(fireImmediately = true) { beatSource ->
            val beatData = beatSource.getBeatData()
            update(beatData)
            setUniforms(beatData)
        }
        withCleanup { observer.remove() }
    }

    card(Styles.card on PaperStyle.root) {
        div(+Styles.card) {
            shaderPreview {
                attrs.shader = beatLinkVisualizerShader
                attrs.width = 300.px
                attrs.height = 200.px
                attrs.dumpShader = true
            }

            div(+Styles.bpm) { ref = bpmDiv }
            div(+Styles.confidence) { ref = confidenceDiv }
        }
    }
}

object Styles : StyleSheet("plugin-Beatlink", isStatic = true) {
    val card by css {
        display = Display.flex
        flex(1.0, 0.0)

        // Needed because of [SharedGlContext]. TODO: remove that requirement.
        important(::backgroundColor, Color.transparent)
    }

    val div by css {
        position = Position.relative
    }

    val bpm by css {
        position = Position.absolute
        bottom = 0.px
        left = 0.px
        color = Color.white
        backgroundColor = Color.black
        fontWeight = FontWeight.bolder
    }

    val confidence by css {
        position = Position.absolute
        bottom = 0.px
        right = 0.px
        color = Color.white
        backgroundColor = Color.black
        fontWeight = FontWeight.bolder
    }
}

external interface BeatLinkControlProps : Props {
    var controlProps: ControlProps
    var beatLinkControl: OpenBeatLinkControl
}

fun RBuilder.beatLinkControl(handler: RHandler<BeatLinkControlProps>) =
    child(beatLinkControl, handler = handler)