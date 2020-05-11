import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version = "1.3.72"
val coroutines_version = "1.3.6"
val serialization_version = kotlin_version
val serialization_runtime_version = "0.20.0"
val ktor_version = "1.3.2"
val kglVersion = "0.3-baaahs"
val joglVersion = "2.3.2"
val lwjglVersion = "3.2.3"
val spek_version = "2.0.11-rc.1" // custom build with LetValues enabled
val atrium_version = "0.10.0"

buildscript {
    val kotlin_version = "1.3.72"

    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

val os = OperatingSystem.current()
val lwjglNatives = when {
    os.isLinux -> "natives-linux"
    os.isMacOsX -> "natives-macos"
    else -> throw IllegalArgumentException()
}

plugins {
    val kotlin_version = "1.3.72"

    id("org.jetbrains.kotlin.multiplatform") version kotlin_version
    id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("maven-publish")
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://kotlin.bintray.com/ktor")
    maven("https://kotlin.bintray.com/kotlin-js-wrappers")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/fabmax/kool")
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
        }
    }

    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serialization_runtime_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serialization_runtime_version")
                implementation("com.soywiz.korlibs.klock:klock:1.5.0")
                api("com.danielgergely.kgl:kgl-metadata:$kglVersion")
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("spek:spek-dsl:$spek_version")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("io.ktor:ktor-server-core:$ktor_version")
                implementation("io.ktor:ktor-server-netty:$ktor_version")
                implementation("io.ktor:ktor-server-host-common:$ktor_version")
                implementation("io.ktor:ktor-websockets:$ktor_version")
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("com.xenomachina:kotlin-argparser:2.0.7")
                implementation("org.deepsymmetry:beat-link:0.6.1")

                implementation(files("src/jvmMain/lib/ftd2xxj-2.1.jar"))
                implementation(files("src/jvmMain/lib/javax.util.property-2_0.jar")) // required by ftd2xxj
                implementation(files("src/jvmMain/lib/TarsosDSP-2.4-bin.jar")) // sound analysis

                implementation("org.joml:joml:1.9.20")

                implementation("com.danielgergely.kgl:kgl-jvm:$kglVersion")

                // GLSL support via LWJGL:
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
                runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
                implementation("com.danielgergely.kgl:kgl-lwjgl:$kglVersion")

                // GLSL support via JOGL:
                implementation("org.jogamp.gluegen:gluegen-rt-main:$joglVersion")
                implementation("org.jogamp.jogl:jogl-all-main:$joglVersion")
                implementation("com.danielgergely.kgl:kgl-jogl:$kglVersion")
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-junit5"))

                runtimeOnly("org.junit.vintage:junit-vintage-engine:5.6.2")
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
                runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutines_version")
                implementation("io.mockk:mockk:1.9.3")

                // For RunOpenGLTests:
                implementation("org.junit.platform:junit-platform-launcher:1.6.2")
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jsMain by getting {
            kotlin.srcDir("src/jsMain/js")

            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutines_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serialization_runtime_version")
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")

                implementation("com.github.markaren:three.kt:v0.88-ALPHA-7")
                implementation("com.danielgergely.kgl:kgl-js:$kglVersion")

                // kotlin react:
                implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
                implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
                implementation(npm("styled-components"))
                implementation(npm("inline-style-prefixer"))

                implementation(npm("babel-loader", "^8.0.6"))
                implementation(npm("@babel/core", "^7.4.5"))
                implementation(npm("@babel/plugin-proposal-class-properties", "^7.4.4"))
                implementation(npm("@babel/plugin-proposal-object-rest-spread", "^7.4.4"))
                implementation(npm("@babel/preset-env", "^7.4.5"))
                implementation(npm("@babel/preset-react", "^7.0.0"))
                implementation(npm("prettier", "1.19.1"))

                implementation(npm("camera-controls", "^1.12.1"))
                implementation(npm("chroma-js", "^2.0.3"))
                implementation(npm("css-loader", "^2.1.1"))
                implementation(npm("@material-ui/core", "~4.8"))
                implementation(npm("node-sass", "^4.12.0"))
                implementation(npm("react", "^16.13.1"))
                implementation(npm("react-compound-slider", "^2.0.0"))
                implementation(npm("react-dom", "^16.13.1"))
                implementation(npm("react-draggable", "^3.3.0"))
                implementation(npm("react-hot-loader", "^4.11.0"))
                implementation(npm("sass-loader", "^7.1.0"))
                implementation(npm("style-loader", "^0.23.1"))
                implementation(npm("three", "^0.102.1"))
                implementation(npm("@fortawesome/fontawesome-free", "^5.12.1"))
                implementation(npm("react-mosaic-component", "^4.0.0"))
                implementation(npm("resize-observer-polyfill", "^1.5.1"))
                implementation(npm("react-ace", "^8.1.0"))
                implementation(npm("ace-builds", "^1.4.6"))
                implementation(npm("@blueprintjs/core", "^3.24.0"))
                implementation(npm("@blueprintjs/icons", "^3.14.0"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("ch.tutteli.atrium:atrium-fluent-en_GB-js:$atrium_version")
            }
        }

        sourceSets.all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
            }
        }
    }
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
    from("build/js/node_modules/@fortawesome") {
        include("fontawesome-free/css/all.min.css")
        include("fontawesome-free/webfonts/*")
    }

    doLast {
        createResourceFilesList(File(buildDir, "processedResources/js/main"))
    }
}

tasks.named<ProcessResources>("jvmProcessResources") {
    from("build/distributions") { include("sparklemotion.js") }

    doLast {
        createResourceFilesList(File(buildDir, "processedResources/jvm/main"))
    }
}

val dokka by tasks.getting(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"

    configuration {
        jdkVersion = 8
        reportUndocumented = true
    }
}

tasks.create<JavaExec>("runPinkyJvm") {
    dependsOn("compileKotlinJvm")
    dependsOn("jsBrowserDevelopmentWebpack")
    main = "baaahs.PinkyMainKt"

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
    main = "baaahs.BrainMainKt"

    val jvmMain = kotlin.targets["jvm"].compilations["main"] as KotlinCompilationToRunnableFiles
    classpath = files(jvmMain.output) + jvmMain.runtimeDependencyFiles
}

tasks.create<JavaExec>("runBridgeJvm") {
    dependsOn("compileKotlinJvm")
    main = "baaahs.SimulatorBridgeKt"

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
    dependsOn("jsProcessResources", "jsBrowserWebpack")
    from(project.file("build/processedResources/js/main"))
    from(project.file("build/distributions"))
    into("build/classes/kotlin/jvm/main/htdocs")
}

tasks.named<Jar>("jvmJar") {
    dependsOn("packageClientResources")
}

tasks.create<ShadowJar>("shadowJar") {
    dependsOn("jvmJar")
    from(tasks.named<Jar>("jvmJar").get().archiveFile)
    configurations = listOf(project.configurations["jvmRuntimeClasspath"])
    manifest {
        attributes["Main-Class"] = "baaahs.PinkyMainKt"
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
