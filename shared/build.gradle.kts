import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val lwjglAllNatives = listOf(
    "natives-linux",
    "natives-macos-arm64",
    "natives-macos",
    "natives-windows",
    "natives-windows-x86"
)

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.benManesVersions)
    id("maven-publish")
    alias(libs.plugins.checkDependencyUpdates)
    alias(libs.plugins.kotestMultiplatform)
}

group = "org.baaahs"
version = "0.0.1"

fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(19)
        vendor = JvmVendorSpec.ADOPTIUM
    }

    metadata {}

    jvm {}

    js(IR) {
        browser {
            useCommonJs()
            binaries.executable()
        }
    }

    androidTarget {}

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SparkleMotionApp"
            isStatic = true

            freeCompilerArgs += listOf(
                "-Xembed-bitcode",
                "-Xembed-resources"
            )
        }
    }

//    cocoapods {
//    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain {
            kotlin.srcDirs(file(project.layout.buildDirectory.file("generated/ksp/metadata/commonMain/kotlin").get()))

            dependencies {
                implementation(libs.kotlinxCoroutinesCore)
                implementation(libs.kotlinxDatetime)
                implementation(libs.kotlinxSerializationJson)
                implementation(libs.koinCore)
                implementation(libs.expressionsEvaluator)
                api(libs.kgl)
                implementation(projects.rpc)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
//                implementation("io.insert-koin:koin-test:${Versions.koin}")
                implementation(libs.kotestFrameworkEngine)
                implementation(libs.kotestAssertionsCore)
                implementation(libs.kotestAssertionsJson)
                implementation(libs.kotestProperty)
            }
        }

        val serverCommonMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerCio)
                implementation(libs.ktorServerWebsockets)
            }
        }

        jvmMain {
            dependsOn(serverCommonMain)

            dependencies {
                implementation(libs.kotlinxCoroutinesDebug)
                implementation(libs.kotlinxCli)
                implementation(libs.ktorServerCore)
//                implementation(libs.ktorServerNetty)
                implementation(libs.ktorServerHostCommon)
                implementation(libs.ktorServerCallLogging)
                implementation(libs.ktorServerWebsockets)
                implementation(libs.logback)
                implementation(libs.beatLink)

                // DMX
                implementation(libs.ftd2xx)

                // Java 3D maths
                implementation(libs.joml)

                // GLSL support via LWJGL:
                implementation(libs.lwjglGlfw)
                implementation(libs.lwjglOpengl)
                lwjglAllNatives.forEach { platform ->
                    runtimeOnly("org.lwjgl:lwjgl:${Versions.lwjgl}:$platform")
                    runtimeOnly("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}:$platform")
                    runtimeOnly("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}:$platform")
                }
                implementation(libs.kglLwjgl)

                // GLSL support via JOGL:
//                implementation("org.jogamp.gluegen:gluegen-rt-main:${Versions.jogl}")
//                implementation("org.jogamp.jogl:jogl-all-main:${Versions.jogl}")
//                implementation("com.danielgergely.kgl:kgl-jogl:${Versions.kgl}")

                // MDNS support:
                implementation(libs.jmdns)

                // To support animated GIFs:
                implementation(libs.animatedGifLib)

                // SoundAnalysisPlugin:
                implementation(libs.tarsosDspCore)
                implementation(libs.tarsosDspJvm)

                // VideoInPlugin:
                implementation(libs.webcamCapture)
                implementation(libs.webcamCaptureDriverNative)
            }
        }
        jvmTest {
            dependencies {
                implementation(project.dependencies.platform("org.junit:junit-bom:${Versions.junit}"))
                implementation(libs.kotestRunnerJunit5)
                runtimeOnly(libs.kotlinReflect)

                implementation(libs.mockk)

                // For RunOpenGLTests:
                implementation(libs.junitPlatformLauncher)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.kotlinxCoroutinesCore)
                implementation(libs.kotlinxHtmlJs)

                implementation(libs.kglJs)

                // kotlin react:
                implementation(project.dependencies.enforcedPlatform(kotlinw("wrappers-bom:${Versions.kotlinWrappers}")))
                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("styled-next"))
                implementation(kotlinw("mui-material"))
                implementation(kotlinw("mui-icons-material"))
                implementation(kotlinw("emotion"))

                // ThreeJS:
                implementation(projects.ext.three)
                implementation(npm("camera-controls", "^1.35.0"))

                // TODO: Revert when https://github.com/john-doherty/long-press-event 2.5 is released.
//                implementation(npm("long-press-event", "^2.4.4"))

                // For diagnostics:
                implementation(npm("dagre-d3", "^0.6.4"))

                implementation(npm("clsx", "^2.0.0"))
                implementation(npm("react-draggable", "^4.4.4"))
                implementation(npm("react-dropzone", "^14.2.1"))
                implementation(npm("react-mosaic-component", "^6.1.0"))
                implementation(npm("react-error-boundary", "^2.2.2"))
                implementation(npm("ace-builds", "1.28.0"))
                implementation(npm("react-ace", "10.1.0"))
                implementation(npm("markdown-it", "~11.0.0"))

                // To support animated GIFs:
                implementation(npm("gifuct-js", "2.1.2"))

                // Used by GridLayout:
                implementation(npm("react-resizable", "3.0.4"))
                implementation(npm("lodash.isequal", "4.5.0"))

                // Used by slider view:
                implementation(npm("d3-array", "^3.2.4"))

                implementation(devNpm("webpack-bundle-analyzer", "4.10.2"))
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        androidMain {
            dependsOn(serverCommonMain)

            dependencies {
                implementation(libs.kglAndroid)
                implementation(libs.koinCore)
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerCio)
                implementation(libs.ktorServerHostCommon)
                implementation(libs.ktorServerCallLogging)
                implementation(libs.ktorServerWebsockets)
                implementation(libs.jmdns)

                // Java 3D maths
                implementation(libs.joml)
            }
        }

        iosMain {
            dependsOn(serverCommonMain)

            dependencies {
                implementation(libs.kglIos)
                implementation(libs.koinCore)
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerCio)
                implementation(libs.ktorServerHostCommon)
//                implementation(libs.ktorServerCallLogging)
                implementation(libs.ktorServerWebsockets)
            }

            resources.srcDirs("src/iosMain/resources",
//                rootProject.file("shared/src/jsMain/resources"),
//                rootProject.file("shared/src/commonMain/resources"),
                rootProject.file("shared/build/processedResources/js/main"),
                rootProject.file("shared/build/kotlin-webpack/js/developmentExecutable")
            )
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

dependencies {
    kspCommonMainMetadata(projects.rpc.processor)
}

android {
    namespace = "baaahs.app.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

val isProductionBuild = project.hasProperty("isProduction")

// include JS artifacts in any JAR we generate
tasks.named<Jar>("jvmJar").configure {
    val taskName = if (isProductionBuild || project.gradle.startParameter.taskNames.contains("installDist")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }

    // bring output file along into the JAR
    val webpackTask = tasks.named<KotlinWebpack>(taskName)
    from(webpackTask.map { it.outputDirectory.file(it.mainOutputFileName) }) {
        into("htdocs")
    }
}

tasks.withType(Kotlin2JsCompile::class) {
    compilerOptions.sourceMap = true
    compilerOptions.sourceMapEmbedSources = JsSourceMapEmbedMode.SOURCE_MAP_SOURCE_CONTENT_ALWAYS
}

tasks.named<ProcessResources>("jsProcessResources") {
    dependsOn(":kotlinNpmInstall") // for node_modules stuff

    from("build/js/node_modules/requirejs") { include("require.js") }
    from("build/js/node_modules/long-press-event/dist") { include("long-press-event.min.js") }
    from("build/js/node_modules/react-grid-layout") {
        into("react-grid-layout")
        include("css/styles.css")
    }

    doLast {
        createResourceFilesList(buildDir("processedResources/js/main"))
    }
}

tasks.named<ProcessResources>("jvmProcessResources") {
    val jsTask = tasks.named<ProcessResources>("jsProcessResources")
    dependsOn(jsTask)

    from(jsTask.map { it.destinationDir }) {
        into("htdocs")
    }

    doLast {
        createResourceFilesList(buildDir("processedResources/jvm/main"))
    }
}

tasks.create<ProcessResources>("iosProcessResources") {
    val jsTask = tasks.named<ProcessResources>("jsProcessResources")
    dependsOn(jsTask)
    destinationDir = buildDir("processedResources/ios/main")

    from(jsTask.map { it.destinationDir }) {
        into("htdocs")
    }

    doLast {
        createResourceFilesList(buildDir("processedResources/ios/main"))
    }
}

tasks.withType<ProcessResources> {
    if (this.name.matches(Regex("ios.*ProcessResources"))) {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

afterEvaluate {
    val jsProcessResourcesTask = tasks.named<ProcessResources>("jsProcessResources")

    val taskName = if (isProductionBuild || project.gradle.startParameter.taskNames.contains("installDist")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }

    // bring output file along into the JAR
    val webpackTask = tasks.named<KotlinWebpack>(taskName)

    tasks.named<KotlinCompile>("compileDebugKotlinAndroid") {
        dependsOn(jsProcessResourcesTask)
        dependsOn(webpackTask)

        jsProcessResourcesTask.map { it.destinationDir }.get().let {
            println("jsTask: $it")
        }
        webpackTask.map { it.outputDirectory.file(it.mainOutputFileName) }.get().get().let {
            println("webpackTask: $it")
        }

//        from(webpackTask.map { it.outputDirectory.file(it.mainOutputFileName) }) {
//            into("htdocs")
//        }
    }

    tasks.named("embedAndSignAppleFrameworkForXcode") {
        println("iOS == ${this::class.java.name}")

        dependsOn(jsProcessResourcesTask)
        dependsOn(webpackTask)
        dependsOn("iosProcessResources")

        jsProcessResourcesTask.map { it.destinationDir }.get().let {
            println("iOS: jsTask: $it")
        }
        webpackTask.map { it.outputDirectory.file(it.mainOutputFileName) }.get().get().let {
            println("iOS: webpackTask: $it")
        }
    }
}

tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(buildDir("javadoc"))
}

tasks.create<JavaExec>("runGlslJvmTests") {
    dependsOn("compileTestKotlinJvm")
    mainClass.set("baaahs.RunOpenGLTestsKt")

    val jvmTest = kotlin.targets["jvm"].compilations["test"] as KotlinCompilation
    classpath = files(jvmTest.output) + jvmTest.runtimeDependencyFiles!!
    if (isMac()) {
        jvmArgs = listOf("-XstartOnFirstThread") // required for OpenGL: https://github.com/LWJGL/lwjgl3/issues/311
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        excludeTags("glsl")
    }

    val outputDir = reports.junitXml.outputLocation
    reports.junitXml.required.set(false)
    jvmArgumentProviders += CommandLineArgumentProvider {
        listOf(
            "-Djunit.platform.reporting.open.xml.enabled=true",
            "-Djunit.platform.reporting.output.dir=${outputDir.get().asFile.absolutePath}"
        )
    }
}

afterEvaluate {
    tasks.named("testDebugUnitTest") { enabled = false }
    tasks.named("testReleaseUnitTest") { enabled = false }
}

tasks.named<Test>("jvmTest") {
//    dependsOn("runGlslJvmTests")
}

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean =
        "eap|alpha|beta|rc".toRegex().containsMatchIn(version.lowercase())

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

// Janky. See https://github.com/google/ksp/issues/963#issuecomment-1780330007.
gradle.projectsEvaluated {
    tasks {
        val kspCommonMainKotlinMetadata by getting
        withType<KotlinCompilationTask<*>> {
            if (this !== kspCommonMainKotlinMetadata) {
                dependsOn(kspCommonMainKotlinMetadata)
            }
        }
    }
}

fun Project.buildDir(path: String) = layout.buildDirectory.file(path).get().asFile