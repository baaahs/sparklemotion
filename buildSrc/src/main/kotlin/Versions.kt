object Versions {
    const val kotlin = "1.6.0"
    const val kotlinNext = "1.6.10"
    const val coroutines = "1.5.2"
    const val serializationRuntime = "1.3.1"
    const val koin = "3.1.3"
    const val dokka = kotlin

    // GL:
    const val kgl = "0.3-baaahs.1"
    const val jogl = "2.3.2"
    const val lwjgl = "3.2.3"

    // JVM:
    const val ktor = "1.6.5"

    // JS:
    const val kotlinxHtml = "0.7.3"
    const val wrappersBuild = "pre.268-kotlin-$kotlin"
    const val kotlinReact = "17.0.2-$wrappersBuild"
    const val kotlinStyled = "5.3.3-$wrappersBuild"
    const val kotlinMaterialUi = "0.7.0"

    // Test:
    const val junit = "5.8.1"
    const val junitPlatform = "1.8.1"
    const val spek = "2.0.11-rc.1" // custom build with LetValues enabled
    const val mockk = "1.12.1"
    const val atrium = "0.16.0"
    const val atriumApi = "atrium-fluent-en_GB"
    const val coroutinesTest = coroutines
}