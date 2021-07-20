object Versions {
    const val kotlin = "1.5.21"
    const val coroutines = "1.4.1"
    const val serializationRuntime = "1.0.1"
    const val ktor = "1.4.1"
    const val koin = "3.0.2"

    // GL:
    const val kgl = "0.3-baaahs.1"
    const val jogl = "2.3.2"
    const val lwjgl = "3.2.3"

    // JS:
    const val wrappersBuild = "pre.218-kotlin-$kotlin"
    const val kotlinWrappers = "0.0.1-$wrappersBuild"
    const val kotlinReact = "17.0.2-$wrappersBuild"
    const val kotlinxHtml = "0.7.3"
    const val styledComponents = "5.3.0"
    const val kotlinStyled = "$styledComponents-$wrappersBuild"
    const val kotlinMaterialUi = "0.6.0"

    // Test:
    const val junit = "5.7.0"
    const val spek = "2.0.11-rc.1" // custom build with LetValues enabled
    const val mockk = "1.10.2"
    const val atrium = "0.14.0"
    const val atriumApi = "atrium-fluent-en_GB"
    const val coroutinesTest = "1.4.1"
}