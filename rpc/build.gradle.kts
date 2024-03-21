plugins {
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.google.devtools.ksp") version Versions.ksp
}

group = "org.baaahs"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/robolectric/spek/mvnrepo/")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    explicitApi()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":net"))
                implementation(kotlinx("coroutines-core", Versions.coroutines))
                implementation(kotlinx("serialization-json", Versions.serializationRuntime))
            }
        }

        val commonTest by getting {
            kotlin.srcDirs(file(project.layout.buildDirectory.file("generated/ksp/jvm/jvmTest/kotlin").get()))

            dependencies {
                implementation(kotlin("test"))
                implementation(kotlinx("coroutines-test", Versions.coroutines))
                implementation("spek:spek-dsl:${Versions.spek}")
                implementation("ch.tutteli.atrium:${Versions.atriumApi}-common:${Versions.atrium}")
                implementation(project(":rpc:processor"))
            }
        }

        val jvmTest by getting {
            dependencies {
                runtimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}")
                implementation(project.dependencies.platform("org.junit:junit-bom:${Versions.junit}"))
                implementation("ch.tutteli.atrium:${Versions.atriumApi}:${Versions.atrium}")

                // TODO: Move back to commonTest when ksp handles test stuff better.
                implementation(project(":rpc:processor"))
            }
        }
    }
}

dependencies {
    add("kspJvmTest", project(":rpc:processor"))
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines.add("junit-jupiter")
        includeEngines.add("spek2")
        excludeTags("glsl")
    }
}