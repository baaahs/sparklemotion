import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinGradlePlugin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}")
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}

val os = OperatingSystem.current()
val lwjglNatives = when {
    os.isLinux -> "natives-linux"
    os.isMacOsX -> "natives-macos"
    else -> throw IllegalArgumentException()
}

plugins {
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("org.jetbrains.dokka") version Versions.dokka
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("maven-publish")
    id("name.remal.check-dependency-updates") version "1.0.211"

    // Workaround for https://youtrack.jetbrains.com/issue/KT-51921. TODO: remove when fixed!
    id("io.github.turansky.kfc.legacy-union") version "5.8.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    maven("https://jitpack.io")
    maven("https://raw.githubusercontent.com/baaahs/kgl/mvnrepo")
    maven("https://raw.githubusercontent.com/robolectric/spek/mvnrepo/")
//    maven("https://maven.danielgergely.com/repository/releases") TODO when next kgl is released
}

group = "org.baaahs"
version = "0.0.1"

kotlin {
    jvm()
    js {
        browser {
            useCommonJs()

            webpackTask {
                report = true
                sourceMaps = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }

        binaries.executable()
    }

    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serializationRuntime}")
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
                implementation("io.insert-koin:koin-core:${Versions.koin}")
                implementation("com.soywiz.korlibs.klock:klock:2.1.2")
                api("com.danielgergely.kgl:kgl-metadata:${Versions.kgl}")
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
//                implementation("io.insert-koin:koin-test:${Versions.koin}")
                implementation("spek:spek-dsl:${Versions.spek}")
                implementation("ch.tutteli.atrium:${Versions.atriumApi}-common:${Versions.atrium}")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${Versions.coroutines}")
                implementation("io.ktor:ktor-server-core:${Versions.ktor}")
                implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
                implementation("io.ktor:ktor-server-host-common:${Versions.ktor}")
                implementation("io.ktor:ktor-websockets:${Versions.ktor}")
                implementation("ch.qos.logback:logback-classic:1.2.7")
                implementation("org.deepsymmetry:beat-link:0.6.3")

                implementation(files("src/jvmMain/lib/ftd2xxj-2.1.jar"))
                implementation(files("src/jvmMain/lib/javax.util.property-2_0.jar")) // required by ftd2xxj
                implementation(files("src/jvmMain/lib/TarsosDSP-2.4-bin.jar")) // sound analysis

                implementation("org.joml:joml:1.9.25")

                implementation("com.danielgergely.kgl:kgl-jvm:${Versions.kgl}")

                // GLSL support via LWJGL:
                implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}")
                implementation("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}")
                runtimeOnly("org.lwjgl:lwjgl:${Versions.lwjgl}:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}:$lwjglNatives")
                implementation("com.danielgergely.kgl:kgl-lwjgl:${Versions.kgl}")

                // GLSL support via JOGL:
                implementation("org.jogamp.gluegen:gluegen-rt-main:${Versions.jogl}")
                implementation("org.jogamp.jogl:jogl-all-main:${Versions.jogl}")
                implementation("com.danielgergely.kgl:kgl-jogl:${Versions.kgl}")

                implementation("org.jmdns:jmdns:3.5.7")
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                runtimeOnly("org.junit.vintage:junit-vintage-engine:${Versions.junit}")
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}")
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")

                implementation("io.mockk:mockk:${Versions.mockk}")
                implementation("ch.tutteli.atrium:${Versions.atriumApi}:${Versions.atrium}")

                // For RunOpenGLTests:
                implementation("org.junit.platform:junit-platform-launcher:${Versions.junitPlatform}")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jsMain by getting {
            kotlin.srcDir("src/jsMain/js")

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:${Versions.kotlinxHtml}")

                implementation("com.danielgergely.kgl:kgl-js:${Versions.kgl}")

                // kotlin react:
//                implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:${Versions.kotlinWrappers}"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:${Versions.kotlinReact}")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:${Versions.kotlinReact}")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:${Versions.kotlinStyled}")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui:${Versions.kotlinMui}")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons:${Versions.kotlinMuiIcons}")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:${Versions.kotlinEmotion}")

                implementation(npm("camera-controls", "^1.25.3"))

                implementation(npm("long-press-event", "^2.4.4"))

                // For diagnostics:
                implementation(npm("dagre-d3", "^0.6.4"))

                // TODO: re-enable when https://github.com/atlassian/react-beautiful-dnd/pull/1890 is addressed
//                implementation(npm("react-beautiful-dnd", "^13.0.0"))
                // <react-beautiful-dnd bug workaround>
                implementation(npm("css-box-model", "^1.2.0"))
                implementation(npm("raf-schd", "^4.0.2"))
                implementation(npm("react-redux", "^7.2.0"))
                implementation(npm("use-memo-one", "^1.1.1"))
                implementation(npm("memoize-one", "^5.1.1"))
                // </react-beautiful-dnd bug workaround>

                implementation(npm("react-compound-slider", "^3.3.1"))
                implementation(npm("react-draggable", "^4.4.4"))
                implementation(npm("react-dropzone", "^14.2.1"))
                implementation(npm("three", "^0.120.0", generateExternals = false))
                implementation(npm("@fortawesome/fontawesome-free", "^5.12.1"))
                implementation(npm("react-mosaic-component", "^4.0.0"))
                implementation(npm("react-error-boundary", "^2.2.2"))
                implementation(npm("react-ace", "^9.0.0"))
                implementation(npm("ace-builds", "^1.4.11"))
                implementation(npm("markdown-it", "~11.0.0"))
                implementation(npm("@blueprintjs/core", "^3.24.0"))
                implementation(npm("@blueprintjs/icons", "^3.14.0"))

                // used by GridLayout:
                implementation(npm("react-resizable", "3.0.4"))
                implementation(npm("lodash.isequal", "4.5.0"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
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

val webpackTask =
    if (project.hasProperty("isProduction")) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }

// workaround for https://youtrack.jetbrains.com/issue/KT-24463:
tasks.named<KotlinCompile>("compileKotlinJvm") {
    dependsOn(":copySheepModel")
}

tasks.create<Copy>("copySheepModel") {
    from("src/commonMain/resources")
    into("build/classes/kotlin/jvm/main")
}

tasks.withType(Kotlin2JsCompile::class) {
    kotlinOptions.sourceMap = true
    kotlinOptions.sourceMapEmbedSources = "always"
}

tasks.withType(KotlinWebpack::class) {
    sourceMaps = true
    inputs.dir("src/jsMain/js")
}

tasks.withType(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.named<ProcessResources>("jsProcessResources") {
    dependsOn("kotlinNpmInstall") // for node_modules stuff

    from("build/js/node_modules/requirejs") { include("require.js") }
    from("build/js/node_modules/three/build") { include("three.js") }
    from("build/js/node_modules/long-press-event/dist") { include("long-press-event.min.js") }
    from("build/js/node_modules/@fortawesome") {
        include("fontawesome-free/css/all.min.css")
        include("fontawesome-free/webfonts/*")
    }
    from("build/js/node_modules/@blueprintjs") {
        into("blueprintjs")
        include("core/lib/css/blueprint.css")
        include("icons/lib/css/blueprint-icons.css")
    }
    from("build/js/node_modules/react-grid-layout") {
        into("react-grid-layout")
        include("css/styles.css")
    }

    doLast {
        createResourceFilesList(File(buildDir, "processedResources/js/main"))
    }
}

tasks.named<ProcessResources>("jvmProcessResources") {
    dependsOn(webpackTask)

    from("build/distributions") { include("sparklemotion.js") }

    doLast {
        createResourceFilesList(File(buildDir, "processedResources/jvm/main"))
    }
}

tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

tasks.create<JavaExec>("runPinkyJvm") {
    dependsOn("compileKotlinJvm")
    dependsOn(webpackTask)
    main = "baaahs.sm.server.PinkyMainKt"

    systemProperties["java.library.path"] = file("src/jvmMain/jni")

    val jvmMain = kotlin.targets["jvm"].compilations["main"] as KotlinCompilationToRunnableFiles
    classpath = files(jvmMain.output) + jvmMain.runtimeDependencyFiles
    if (isMac()) {
        jvmArgs = listOf(
            "-XstartOnFirstThread", // required for OpenGL: https://github.com/LWJGL/lwjgl3/issues/311
            "-Djava.awt.headless=true" // required for Beat Link; otherwise we get this: https://jogamp.org/bugzilla/show_bug.cgi?id=485
        )
    }
}

tasks.create<JavaExec>("runBrainJvm") {
    dependsOn("compileKotlinJvm")
    main = "baaahs.sm.brain.sim.BrainMainKt"

    val jvmMain = kotlin.targets["jvm"].compilations["main"] as KotlinCompilationToRunnableFiles
    classpath = files(jvmMain.output) + jvmMain.runtimeDependencyFiles
}

tasks.create<JavaExec>("runBridgeJvm") {
    dependsOn("compileKotlinJvm")
    main = "baaahs.sm.bridge.SimulatorBridgeKt"

    val jvmMain = kotlin.targets["jvm"].compilations["main"] as KotlinCompilationToRunnableFiles
    classpath = files(jvmMain.output) + jvmMain.runtimeDependencyFiles
}

tasks.create<JavaExec>("runGlslJvmTests") {
    dependsOn("compileTestKotlinJvm")
    main = "baaahs.RunOpenGLTestsKt"

    val jvmTest = kotlin.targets["jvm"].compilations["test"] as KotlinCompilationToRunnableFiles
    classpath = files(jvmTest.output) + jvmTest.runtimeDependencyFiles
    if (isMac()) {
        jvmArgs = listOf("-XstartOnFirstThread") // required for OpenGL: https://github.com/LWJGL/lwjgl3/issues/311
    }
}

tasks.create<Copy>("packageClientResources") {
    dependsOn("jsProcessResources", webpackTask)
    duplicatesStrategy = DuplicatesStrategy.WARN
    from(project.file("build/processedResources/js/main"))
    from(project.file("build/distributions"))
    into("build/classes/kotlin/jvm/main/htdocs")
}

tasks.named<Jar>("jvmJar") {
    dependsOn("packageClientResources")
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.create<ShadowJar>("shadowJar") {
    dependsOn("jvmJar")
    from(tasks.named<Jar>("jvmJar").get().archiveFile)
    configurations = listOf(project.configurations["jvmRuntimeClasspath"])
    manifest {
        attributes["Main-Class"] = "baaahs.sm.server.PinkyMainKt"
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines.add("junit-vintage")
        includeEngines.add("spek2")
    }
}

tasks.named<Test>("jvmTest") {
//    dependsOn("runGlslJvmTests")
}

tasks.withType<DependencyUpdatesTask> {
    fun isNonStable(version: String): Boolean =
        "eap|alpha|beta|rc".toRegex().containsMatchIn(version.toLowerCase())

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
