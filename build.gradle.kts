import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinGradlePlugin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}")
        classpath("com.github.johnrengelman:shadow:8.1.1")
    }
}

val lwjglAllNatives = listOf(
    "natives-linux",
    "natives-macos-arm64",
    "natives-macos",
    "natives-windows",
    "natives-windows-x86"
)

plugins {
    application
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("org.jetbrains.dokka") version Versions.dokka
    id("com.google.devtools.ksp") version Versions.ksp
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("maven-publish")
    id("name.remal.check-dependency-updates") version "1.0.211"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    maven("https://raw.githubusercontent.com/robolectric/spek/mvnrepo/")
    maven("https://maven.danielgergely.com/releases")
    maven("https://jitpack.io")
    maven("https://mvn.0110.be/releases") // TarsosDSP
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

    jvm {
        withJava()
    }

    js(IR) {
        browser {
            useCommonJs()
            binaries.executable()
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDirs(file(project.layout.buildDirectory.file("generated/ksp/metadata/commonMain/kotlin").get()))

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serializationRuntime}")
                implementation("io.insert-koin:koin-core:${Versions.koin}")
                implementation("io.github.murzagalin:multiplatform-expressions-evaluator:0.15.0")
                api("com.danielgergely.kgl:kgl:${Versions.kgl}")
                implementation(project(":rpc"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
//                implementation("io.insert-koin:koin-test:${Versions.koin}")
                implementation("spek:spek-dsl:${Versions.spek}")
                implementation("ch.tutteli.atrium:${Versions.atriumApi}-common:${Versions.atrium}")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${Versions.coroutines}")
                implementation("io.ktor:ktor-server-core:${Versions.ktor}")
                implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
                implementation("io.ktor:ktor-server-host-common:${Versions.ktor}")
                implementation("io.ktor:ktor-server-call-logging:${Versions.ktor}")
                implementation("io.ktor:ktor-server-websockets:${Versions.ktor}")
                implementation("ch.qos.logback:logback-classic:1.3.11")
                implementation("org.deepsymmetry:beat-link:7.2.0")
                implementation("com.illposed.osc:javaosc-core:0.9")

                implementation(files("src/jvmMain/lib/ftd2xxj-2.1.jar"))
                implementation(files("src/jvmMain/lib/javax.util.property-2_0.jar")) // required by ftd2xxj

                implementation("org.joml:joml:1.9.25")

                // GLSL support via LWJGL:
                implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}")
                implementation("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}")
                lwjglAllNatives.forEach { platform ->
                    runtimeOnly("org.lwjgl:lwjgl:${Versions.lwjgl}:$platform")
                    runtimeOnly("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}:$platform")
                    runtimeOnly("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}:$platform")
                }
                implementation("com.danielgergely.kgl:kgl-lwjgl:${Versions.kgl}")

                // GLSL support via JOGL:
                implementation("org.jogamp.gluegen:gluegen-rt-main:${Versions.jogl}")
                implementation("org.jogamp.jogl:jogl-all-main:${Versions.jogl}")
//                implementation("com.danielgergely.kgl:kgl-jogl:${Versions.kgl}")

                // MDNS support:
                implementation("org.jmdns:jmdns:3.5.7")

                // To support animated GIFs:
                implementation("com.madgag:animated-gif-lib:1.4")

                // SoundAnalysisPlugin:
                implementation("be.tarsos.dsp:core:2.5")
                implementation("be.tarsos.dsp:jvm:2.5")

                // VideoInPlugin:
                implementation("com.github.sarxos:webcam-capture:0.3.12")
                implementation("io.github.eduramiba:webcam-capture-driver-native:1.0.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project.dependencies.platform("org.junit:junit-bom:${Versions.junit}"))
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}")
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")

                implementation("io.mockk:mockk:${Versions.mockk}")
                implementation("ch.tutteli.atrium:${Versions.atriumApi}:${Versions.atrium}")

                // For RunOpenGLTests:
                implementation("org.junit.platform:junit-platform-launcher:${Versions.junitPlatform}")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:${Versions.kotlinxHtml}")

                implementation("com.danielgergely.kgl:kgl-js:${Versions.kgl}")

                // kotlin react:
                implementation(project.dependencies.enforcedPlatform(kotlinw("wrappers-bom:${Versions.kotlinWrappers}")))
                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("styled-next"))
                implementation(kotlinw("mui-material"))
                implementation(kotlinw("mui-icons-material"))
                implementation(kotlinw("emotion"))

                // ThreeJS:
                implementation(project(":ext:three"))
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
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("ch.tutteli.atrium:${Versions.atriumApi}-js:${Versions.atrium}")
            }
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
    add("kspCommonMainMetadata", project(":rpc:processor"))
}

val isProductionBuild = project.hasProperty("isProduction")

application {
    mainClass.set("baaahs.sm.server.PinkyMainKt")

    applicationDefaultJvmArgs += "-Djava.library.path=${file("src/jvmMain/jni")}"

    if (!isProductionBuild) {
        applicationDefaultJvmArgs += "-Dio.ktor.development=true"
    }

    if (isMac()) {
        applicationDefaultJvmArgs += listOf(
            "-XstartOnFirstThread", // required for OpenGL: https://github.com/LWJGL/lwjgl3/issues/311
            "-Djava.awt.headless=true" // required for Beat Link; otherwise we get this: https://jogamp.org/bugzilla/show_bug.cgi?id=485
        )
    }
}

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
    dependsOn("kotlinNpmInstall") // for node_modules stuff

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

tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(buildDir("javadoc"))
}

// This task is deprecated, use `run` instead.
tasks.create("runPinkyJvm") {
    dependsOn("run")
}

tasks.create<JavaExec>("runBrainJvm") {
    dependsOn("compileKotlinJvm")
    mainClass.set("baaahs.sm.brain.sim.BrainMainKt")

    val jvmMain = kotlin.targets["jvm"].compilations["main"] as KotlinCompilation
    classpath = files(jvmMain.output) + jvmMain.runtimeDependencyFiles!!
}

tasks.create<JavaExec>("runBridgeJvm") {
    dependsOn("compileKotlinJvm")
    mainClass.set("baaahs.sm.bridge.SimulatorBridgeKt")

    val jvmMain = kotlin.targets["jvm"].compilations["main"] as KotlinCompilation
    classpath = files(jvmMain.output) + jvmMain.runtimeDependencyFiles!!
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

tasks.named<JavaExec>("run").configure {
    classpath(tasks.named<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("spek2")
        excludeTags("glsl")
    }
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