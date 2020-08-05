package baaahs.glshaders

object ShaderFactory {
    fun colorShader(title: String): ColorShader {
        return GlslAnalyzer().openShader("""
            // $title
            uniform float time;
            uniform vec2  resolution;
            uniform float blueness;
            int someGlobalVar;
            const int someConstVar = 123;
            
            int anotherFunc(int i) { return i; }
            
            void main( void ) {
                vec2 uv = gl_FragCoord.xy / resolution.xy;
                someGlobalVar = anotherFunc(someConstVar);
                gl_FragColor = vec4(uv.xy, blueness, 1.);
            }
        """.trimIndent()) as ColorShader
    }
}