package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.getBang
import baaahs.gl.patch.PatchResolver
import baaahs.jsx.useResizeListener
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenVisualizerControl
import baaahs.ui.on
import baaahs.ui.xComponent
import baaahs.visualizer.SurfaceGeometry
import kotlinext.js.jsObject
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.*
import three.js.ShaderMaterial
import three.js.Vector2
import three.js.Vector3
import kotlin.random.Random

val Visualizer = xComponent<VisualizerProps>("Visualizer") { props ->
    val appContext = useContext(appContext)

    val rootEl = ref<Element>()
    val visualizer by state {
        val model = appContext.webClient.model
        val openShow = appContext.webClient.openShow!!
        baaahs.visualizer.Visualizer(model, appContext.clock)
            .also { viz ->
                model.allSurfaces.forEach { surface ->
                    val vizSurface = viz.addSurface(SurfaceGeometry(surface))
                    vizSurface.faceMaterial = material
                    // TODO: Bind this to renderer output.
                }

                viz.addPreRenderListener {
                    appContext.showPlayer
                    val patchResolution = PatchResolver(
                        renderTargets.values, activePatchSet)
                    currentRenderPlan = patchResolution.createRenderPlan(openShow.allDataSources) { _, dataSource ->
                        openShow.feeds.getBang(dataSource, "data feed")
                    }

                    val brightness = Random.nextFloat()
                    with(material.uniforms.asDynamic()) {
                        in_brightnessSlider.value = brightness
                    }
                    material.uniformsNeedUpdate = true
                }
            }
            .facade
    }
    visualizer.rotate = props.visualizerControl.rotate

    onMount {
        visualizer.container = rootEl.current as HTMLDivElement
        withCleanup {
            visualizer.container = null
        }
    }

    useResizeListener(rootEl) {
        visualizer.resize()
    }

    card(Styles.visualizerCard on PaperStyle.root) {
        ref = rootEl
    }
}

external interface VisualizerProps : RProps {
    var controlProps: ControlProps
    var visualizerControl: OpenVisualizerControl
}

fun RBuilder.visualizer(handler: RHandler<VisualizerProps>) =
    child(Visualizer, handler = handler)


val material = ShaderMaterial(jsObject {
    // language=glsl
    vertexShader = """
        varying vec3 xyzPosition;
        
        void main() {
            vec4 xyz = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
            gl_Position = xyz;
            xyzPosition = position;
        }
    """

    // language=glsl
    fragmentShader = """
        #ifdef GL_ES
        precision mediump float;
        #endif

        // SparkleMotion-generated GLSL

        varying vec3 xyzPosition;
        
        struct ModelInfo {
            vec3 center;
            vec3 extents;
        };

        // Data source: Brightness Slider
        uniform float in_brightnessSlider;

        // Data source: Model Info
        uniform ModelInfo in_modelInfo;

        vec3 in_pixelLocation;

        // Data source: Resolution
        uniform vec2 in_resolution;

        // Data source: Saturation Slider
        uniform float in_saturationSlider;

        // Data source: Size Slider
        uniform float in_sizeSlider;

        // Shader: Flat Projection; namespace: p0
        // Flat Projection

        vec2 p0_flatProjectioni_result = vec2(0.);

        #line 11
        const float p0_flatProjection_PI = 3.141592654;

        #line 15
        vec2 p0_flatProjection_main(vec3 pixelLocation) {
            vec3 pixelOffset = (pixelLocation - in_modelInfo.center) / in_modelInfo.extents + .5;
            return vec2(1.-pixelOffset.x, pixelOffset.y);
            // vec3 normalDelta = normalize(pixelOffset);
            // float theta = atan(abs(normalDelta.z), normalDelta.x); // theta in range [-π,π]
            // if (theta < 0.0) theta += (2.0f * PI);                 // theta in range [0,2π)
            // float u = theta / (2.0f * PI);                         // u in range [0,1)
            // float v = (pixelOffset.y + modelInfo.extents.y / 2.0f) / modelInfo.extents.y;
            // return vec2(u, v);
        }

        // Shader: Scale; namespace: p1
        // Scale

        vec2 p1_scalei_result = vec2(0.);

        #line 5
        vec2 p1_scale_main(vec2 uvIn) {
          return (uvIn - .5) / in_sizeSlider + .5;
        }

        // Shader: GLSL Hue Test Pattern; namespace: p2
        // GLSL Hue Test Pattern

        vec4 p2_glslHueTestPattern_gl_FragColor = vec4(0., 0., 0., 1.);

        #line 2
        void p2_glslHueTestPattern_main(void) {
            p2_glslHueTestPattern_gl_FragColor = vec4(p1_scalei_result.xy / in_resolution, 0.0, 1.0);
        }

        // Shader: Saturation; namespace: p3
        // Saturation

        vec4 p3_saturationi_result = vec4(0., 0., 0., 1.);

        #line 4
        vec3 p3_saturation_rgb2hsv(vec3 c)
        {
            vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
            vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
            vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

            float d = q.x - min(q.w, q.y);
            float e = 1.0e-10;
            return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
        }

        #line 17
        vec3 p3_saturation_hsv2rgb(vec3 c)
        {
            vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
            vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
            return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
        }

        #line 26
        vec4 p3_saturation_main(vec4 inColor) {
            if (in_saturationSlider == 1.) return inColor;

            vec4 clampedColor = clamp(inColor, 0., 1.);
            vec3 hsv = p3_saturation_rgb2hsv(clampedColor.rgb);
            hsv.y *= in_saturationSlider;
            return vec4(p3_saturation_hsv2rgb(hsv), clampedColor.a);
        }

        // Shader: Brightness; namespace: p4
        // Brightness

        vec4 p4_brightnessi_result = vec4(0., 0., 0., 1.);

        #line 5
        vec4 p4_brightness_main(vec4 inColor) {
            vec4 clampedColor = clamp(inColor, 0., 1.);
            return vec4(clampedColor.rgb * in_brightnessSlider, clampedColor.a);
        }


        #line 10001
        void main() {
          // Load Pixel Location
          in_pixelLocation = xyzPosition;

          // Invoke Flat Projection
          p0_flatProjectioni_result = p0_flatProjection_main(in_pixelLocation);

          // Invoke Scale
          p1_scalei_result = p1_scale_main(p0_flatProjectioni_result);

          // Invoke GLSL Hue Test Pattern
          p2_glslHueTestPattern_main();

          // Invoke Saturation
          p3_saturationi_result = p3_saturation_main(p2_glslHueTestPattern_gl_FragColor);

          // Invoke Brightness
          p4_brightnessi_result = p4_brightness_main(p3_saturationi_result);

          gl_FragColor = p4_brightnessi_result;
//          gl_FragColor = vec4(xyzPosition, 1.);
        }
    """.trimIndent()

    val resolution = Vector2(1, 1)

    @Suppress("UnsafeCastFromDynamic")
    uniforms = jsObject<dynamic> {
        in_brightnessSlider = jsObject { value = 1 }

        in_modelInfo = jsObject {
            value = jsObject {
                center = Vector3(-192.4335, 119.6765, 0.361)
                extents = Vector3(447.327, 236.977, 213)
            }
        };

        in_resolution = jsObject { value = resolution }
        in_saturationSlider = jsObject { value = 1 }
        in_sizeSlider = jsObject { value = 1 }
    }
})